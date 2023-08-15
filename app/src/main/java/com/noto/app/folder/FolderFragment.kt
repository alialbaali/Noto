package com.noto.app.folder

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.placeholderItem
import com.noto.app.databinding.FolderFragmentBinding
import com.noto.app.databinding.NoteItemBinding
import com.noto.app.domain.model.*
import com.noto.app.getOrDefault
import com.noto.app.label.LabelItemModel
import com.noto.app.label.labelListItem
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderFragment : Fragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<FolderFragmentArgs>()

    private var epoxyController: EpoxyController? = null

    private var itemTouchHelper: ItemTouchHelper? = null

    private lateinit var layoutManager: StaggeredGridLayoutManager

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val anchorViewId by lazy { R.id.bab_selection }

    private val folderColor by lazy { viewModel.folder.value.color }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FolderFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    @OptIn(FlowPreview::class)
    @Suppress("UNCHECKED_CAST")
    private fun FolderFragmentBinding.setupState() {
        val archiveMenuItem = bab.menu.findItem(R.id.archive)
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        layoutManager = StaggeredGridLayoutManager(
            1,
            StaggeredGridLayoutManager.VERTICAL
        ).also(rv::setLayoutManager)

        combine(
            viewModel.folder
                .map { it.filteringType }
                .distinctUntilChanged(),
            viewModel.notes,
            viewModel.labels,
            viewModel.searchTerm,
        ) { filteringType, notesState, labels, searchTerm ->
            val notes = notesState.getOrDefault(emptyList())
            setupNotesCount(notes, labels, filteringType, searchTerm)
        }.launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.isRememberScrollingPosition,
        ) { folder, isRememberScrollingPosition ->
            setupFolder(folder, isRememberScrollingPosition)
            context?.let { context ->
                val text = context.stringResource(R.string.folder_archive, folder.getTitle(context))
                MenuItemCompat.setTooltipText(archiveMenuItem, text)
                MenuItemCompat.setContentDescription(archiveMenuItem, text)
            }
            folder
        }.distinctUntilChangedBy { folder -> folder.layout }
            .onEach { folder -> setupLayoutManager(folder.layout) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.notes,
            viewModel.labels,
            viewModel.font,
            viewModel.folder,
            viewModel.searchTerm,
            viewModel.isSelection,
        ) { values ->
            val notes = values[0] as UiState<List<NoteItemModel>>
            val labels = values[1] as List<LabelItemModel>
            val font = values[2] as Font
            val folder = values[3] as Folder
            val searchTerm = values[4] as String
            val isSelection = values[5] as Boolean
            val filteredNotes = notes.map {
                it.filterByLabels(labels.filterSelected(), folder.filteringType)
                    .filterBySearchTerm(searchTerm)
            }
            setupNotesAndLabels(
                filteredNotes,
                labels,
                font,
                folder,
                searchTerm,
                isSelection,
            )
            if (isSelection) {
                val isAllSelected =
                    filteredNotes.getOrDefault(emptyList()).all { model -> model.isSelected }
                if (isAllSelected) {
                    fabSelectAll.hideWithAnimation()
                } else {
                    fabSelectAll.showWithAnimation()
                }
            } else {
                fabSelectAll.hideWithAnimation()
            }
        }.launchIn(lifecycleScope)

        viewModel.folder
            .onEach { folder -> setupItemTouchHelper(folder.layout, folder.sortingType) }
            .launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled -> if (isSearchEnabled) enableSearch() else disableSearch() }
            .launchIn(lifecycleScope)

        etSearch.textAsFlow()
            .asSearchFlow()
            .onEach { searchTerm -> viewModel.setSearchTerm(searchTerm) }
            .launchIn(lifecycleScope)

        rv.scrollPositionAsFlow()
            .debounce(DebounceTimeoutMillis)
            .onEach {
                val scrollingPosition =
                    layoutManager.findFirstCompletelyVisibleItemPositions(null).firstOrNull() ?: -1
                if (scrollingPosition != -1) viewModel.updateFolderScrollingPosition(
                    scrollingPosition
                )
            }
            .launchIn(lifecycleScope)

        root.keyboardVisibilityAsFlow()
            .onEach { isKeyboardVisible ->
                fab.isVisible = !isKeyboardVisible
                bab.isVisible = !isKeyboardVisible
            }
            .launchIn(lifecycleScope)

        if (isCurrentLocaleArabic()) {
            tvFolderNotesCount.isVisible = false
            tvFolderNotesCountRtl.isVisible = true
        } else {
            tvFolderNotesCount.isVisible = true
            tvFolderNotesCountRtl.isVisible = false
        }

        viewModel.isSelection
            .onEach { isSelection ->
                if (isSelection) {
                    fab.hideWithAnimation()
                    bab.performHide(true)
                    bab.isVisible = false
                    babSelection.isVisible = true
                    babSelection.performShow(true)
                    fabSelection.showWithAnimation()
                } else {
                    fabSelection.hideWithAnimation()
                    babSelection.performHide(true)
                    babSelection.isVisible = false
                    bab.isVisible = true
                    bab.performShow(true)
                    fab.showWithAnimation()
                }
            }
            .launchIn(lifecycleScope)

        viewModel.notes
            .map { it.getOrDefault(emptyList()).filter { model -> model.isSelected } }
            .onEach { selectedModels ->
                if (selectedModels.count() == 1) {
                    val selectedModel = selectedModels.first()
                    babSelection.replaceMenu(R.menu.folder_single_selection_menu)
                    val reminderDrawable = if (selectedModel.note.reminderDate == null)
                        R.drawable.ic_round_notification_add_24
                    else
                        R.drawable.ic_round_edit_notifications_24
                    babSelection.menu?.findItem(R.id.add_reminder)?.setIcon(reminderDrawable)
                } else {
                    babSelection.replaceMenu(R.menu.folder_multi_selection_menu)
                }
                val pinMenuItem = babSelection.menu?.findItem(R.id.pin)
                if (selectedModels.none { it.note.isPinned }) {
                    pinMenuItem?.setTitle(R.string.pin)
                    pinMenuItem?.setIcon(R.drawable.ic_round_pin_24)
                } else {
                    pinMenuItem?.setTitle(R.string.unpin)
                    pinMenuItem?.setIcon(R.drawable.ic_round_pin_off_24)
                }
            }
            .launchIn(lifecycleScope)

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean?>(Constants.DisableSelection)
            ?.observe(viewLifecycleOwner) {
                viewModel.disableSelection()
                viewModel.deselectAllNotes()
            }
    }

    private fun FolderFragmentBinding.setupListeners() {
        tb.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.notes_view -> navController?.navigateSafely(
                    FolderFragmentDirections.actionFolderFragmentToNoteListViewDialogFragment(
                        args.folderId
                    )
                )
            }
            false
        }

        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

        fab.setOnClickListener {
            val selectedLabelsIds = viewModel.labels.value
                .filterSelected()
                .map { it.id }
                .toLongArray()
            navController?.navigateSafely(
                FolderFragmentDirections.actionFolderFragmentToNoteFragment(
                    args.folderId,
                    labelsIds = selectedLabelsIds,
                    selectedNoteIds = longArrayOf(),
                )
            )
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archive -> setupArchivedNotesMenuItem()
                R.id.search -> setupSearchMenuItem()
                R.id.more -> setupMoreMenuItem()
                else -> false
            }
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment())
        }

        activity?.onBackPressedDispatcher?.addCallback {
            when {
                viewModel.isSelection.value -> if (viewModel.isSearchEnabled.value) {
                    viewModel.disableSearch()
                } else {
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                }

                viewModel.isSearchEnabled.value -> viewModel.disableSearch()
                viewModel.labels.value.any { it.isSelected } -> viewModel.clearLabelSelection()
                viewModel.quickExit.value -> activity?.finish()
                else -> navController?.navigateSafely(
                    FolderFragmentDirections.actionFolderFragmentToMainFragment(
                        exit = true
                    )
                )
            }
        }

        fabSelection.setOnClickListener {
            val notes = viewModel.notes.value.getOrDefault(emptyList())
            val selectedNoteIds = viewModel.selectedNotes
                .map { model -> model.note.id }
                .toLongArray()
            if (selectedNoteIds.count() == 1) {
                navController?.navigateSafely(
                    FolderFragmentDirections.actionFolderFragmentToNoteDialogFragment(
                        args.folderId,
                        selectedNoteIds.first(),
                        R.id.folderFragment,
                        isSelectAllEnabled = notes.size != 1,
                        selectedNoteIds = selectedNoteIds,
                    )
                )
            } else {
                navController?.navigateSafely(
                    FolderFragmentDirections.actionFolderFragmentToNoteSelectionDialogFragment(
                        folderId = args.folderId,
                        selectedNoteIds = selectedNoteIds,
                    )
                )
            }
        }

        babSelection.setOnMenuItemClickListener { menuItem ->
            val selectedNotes = viewModel.selectedNotes.map { model -> model.note }
            val selectedNotesCount = selectedNotes.count()

            when (menuItem.itemId) {
                R.id.add_reminder -> {
                    navController?.navigateSafely(
                        FolderFragmentDirections.actionFolderFragmentToNoteReminderDialogFragment(
                            args.folderId,
                            selectedNotes.first().id,
                        )
                    )
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                    true
                }

                R.id.merge -> {
                    viewModel.mergeSelectedNotes()
                    context?.let { context ->
                        val text =
                            context.stringResource(R.string.notes_are_merged, selectedNotes.count())
                        val drawableId = R.drawable.ic_round_merge_24
                        root.snackbar(text, drawableId, anchorViewId, folderColor)
                        context.updateAllWidgetsData()
                        context.updateNoteListWidgets()
                    }
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                    true
                }

                R.id.pin -> {
                    if (selectedNotes.none { it.isPinned }) {
                        viewModel.pinSelectedNotes()
                        context?.let { context ->
                            val text = context.quantityStringResource(
                                R.plurals.note_is_pinned,
                                selectedNotesCount,
                                selectedNotesCount
                            )
                            val drawableId = R.drawable.ic_round_pin_24
                            root.snackbar(text, drawableId, anchorViewId, folderColor)
                            context.updateAllWidgetsData()
                        }
                    } else {
                        viewModel.unpinSelectedNotes()
                        context?.let { context ->
                            val text = context.quantityStringResource(
                                R.plurals.note_is_unpinned,
                                selectedNotesCount,
                                selectedNotesCount
                            )
                            val drawableId = R.drawable.ic_round_pin_off_24
                            root.snackbar(text, drawableId, anchorViewId, folderColor)
                            context.updateAllWidgetsData()
                        }
                    }
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                    true
                }

                R.id.share -> {
                    launchShareNotesIntent(selectedNotes)
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                    true
                }

                R.id.archive -> {
                    viewModel.archiveSelectedNotes()
                    context?.let { context ->
                        val text = context.quantityStringResource(
                            R.plurals.note_is_archived,
                            selectedNotesCount,
                            selectedNotesCount
                        )
                        val drawableId = R.drawable.ic_round_archive_24
                        root.snackbar(text, drawableId, anchorViewId, folderColor)
                        selectedNotes.filter { note -> note.reminderDate != null }
                            .forEach { note -> alarmManager?.cancelAlarm(context, note.id) }
                        context.updateAllWidgetsData()
                        context.updateNoteListWidgets()
                    }
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                    true
                }

                else -> false
            }
        }

        fabSelectAll.setOnClickListener {
            viewModel.selectAllNotes()
        }
    }

    private fun FolderFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> layoutManager.spanCount = 1
            Layout.Grid -> layoutManager.spanCount = 2
        }
        rv.resetAdapter()
    }

    private fun FolderFragmentBinding.setupNotesAndLabels(
        state: UiState<List<NoteItemModel>>,
        labels: List<LabelItemModel>,
        font: Font,
        folder: Folder,
        searchTerm: String,
        isSelection: Boolean,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator(folder.color)
            is UiState.Success -> {
                val notes = state.value

                rv.withModels {
                    epoxyController = this

                    labelListItem {
                        id("labels")
                        labels(labels)
                        color(folder.color)
                        onAllLabelClickListener { _ ->
                            viewModel.clearLabelSelection()
                        }
                        onLabelClickListener { label ->
                            if (labels.first { it.label.id == label.id }.isSelected)
                                viewModel.deselectLabel(label.id)
                            else
                                viewModel.selectLabel(label.id)
                        }
                        onLabelLongClickListener { label ->
                            navController?.navigateSafely(
                                FolderFragmentDirections.actionFolderFragmentToLabelDialogFragment(
                                    args.folderId,
                                    label.id
                                )
                            )
                            true
                        }
                        onNewLabelClickListener { _ ->
                            navController?.navigateSafely(
                                FolderFragmentDirections.actionFolderFragmentToNewLabelDialogFragment(
                                    args.folderId
                                )
                            )
                        }
                    }

                    context?.let { context ->
                        if (notes.isEmpty()) {
                            val placeholderId = when {
                                labels.any { it.isSelected } && searchTerm.isNotBlank() -> R.string.no_relevant_notes_found
                                labels.any { it.isSelected } -> R.string.no_notes_found_labels
                                searchTerm.isNotBlank() -> R.string.no_notes_found_search
                                else -> R.string.folder_is_empty
                            }

                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(placeholderId))
                            }
                        } else {
                            buildNotesModels(
                                context,
                                folder,
                                notes,
                                onCreateClick = {
                                    navController?.navigateSafely(
                                        FolderFragmentDirections.actionFolderFragmentToNoteFragment(
                                            args.folderId,
                                            labelsIds = it.toLongArray(),
                                            selectedNoteIds = longArrayOf(),
                                        )
                                    )
                                }
                            ) { notes ->
                                val noteIds = notes.map { it.note.id }.toLongArray()
                                notes.forEach { model ->
                                    noteItem {
                                        id(model.note.id)
                                        model(model)
                                        font(font)
                                        color(folder.color)
                                        previewSize(folder.notePreviewSize)
                                        isShowCreationDate(folder.isShowNoteCreationDate)
                                        searchTerm(searchTerm)
                                        isManualSorting(folder.sortingType == NoteListSortingType.Manual)
                                        isSelection(isSelection)
                                        onClickListener { _ ->
                                            if (isSelection) {
                                                if (model.isSelected) viewModel.deselectNote(model.note.id) else viewModel.selectNote(
                                                    model.note.id
                                                )
                                            } else {
                                                when (folder.openNotesIn) {
                                                    OpenNotesIn.Editor -> navController?.navigateSafely(
                                                        FolderFragmentDirections.actionFolderFragmentToNoteFragment(
                                                            model.note.folderId,
                                                            noteId = model.note.id,
                                                            selectedNoteIds = noteIds,
                                                            searchTerm = searchTerm.ifBlank { null },
                                                        )
                                                    )

                                                    OpenNotesIn.ReadingMode -> navController?.navigateSafely(
                                                        FolderFragmentDirections.actionFolderFragmentToNotePagerFragment(
                                                            model.note.folderId,
                                                            model.note.id,
                                                            noteIds
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        if (folder.sortingType == NoteListSortingType.Manual) {
                                            onLongClickListener(null as View.OnLongClickListener?)
                                        } else {
                                            onLongClickListener { _ ->
                                                viewModel.enableSelection()
                                                if (model.isSelected)
                                                    viewModel.deselectNote(model.note.id)
                                                else
                                                    viewModel.selectNote(model.note.id)
                                                true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FolderFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        navController?.navigateSafely(
            FolderFragmentDirections.actionFolderFragmentToFolderArchiveFragment(
                args.folderId
            )
        )
        return true
    }

    private fun FolderFragmentBinding.setupSearchMenuItem(): Boolean {
        if (viewModel.isSearchEnabled.value)
            viewModel.disableSearch()
        else
            viewModel.enableSearch()
        return true
    }

    private fun FolderFragmentBinding.setupMoreMenuItem(): Boolean {
        navController?.navigateSafely(
            FolderFragmentDirections.actionFolderFragmentToFolderDialogFragment(
                args.folderId
            )
        )
        return true
    }

    private fun FolderFragmentBinding.setupFolder(
        folder: Folder,
        isRememberScrollingPosition: Boolean
    ) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toColorResourceId())
            val colorStateList = color.toColorStateList()
            tvFolderTitle.text = folder.getTitle(context)
            tvFolderTitle.setTextColor(colorStateList)
            tvFolderNotesCount.typeface = context.tryLoadingFontResource(R.font.nunito_semibold)
            tvFolderNotesCount.animationInterpolator = DefaultInterpolator()
            fab.backgroundTintList = colorStateList
            fabSelection.backgroundTintList = colorStateList
            fabSelectAll.imageTintList = colorStateList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
                fabSelection.outlineAmbientShadowColor = color
                fabSelection.outlineSpotShadowColor = color
                fabSelectAll.outlineAmbientShadowColor = color
                fabSelectAll.outlineSpotShadowColor = color
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                etSearch.textCursorDrawable?.mutate()?.setTint(color)
            }
            if (folder.isArchived || folder.isVaulted) {
                val drawableId =
                    if (folder.isVaulted) R.drawable.ic_round_lock_24 else R.drawable.ic_round_archive_24
                val bitmapDrawable = context.drawableResource(drawableId)
                    ?.mutate()
                    ?.toBitmap(20.dp, 20.dp)
                    ?.toDrawable(resources)
                    ?.also { it.setTint(color) }
                tvFolderTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    bitmapDrawable,
                    null
                )
                tvFolderTitle.compoundDrawablePadding =
                    context.dimenResource(R.dimen.spacing_small).toInt()
            } else {
                tvFolderTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
                tvFolderTitle.compoundDrawablePadding = 0
            }
        }
        rv.post {
            if (!viewModel.isSelection.value) {
                if (isRememberScrollingPosition) {
                    layoutManager.scrollToPosition(folder.scrollingPosition)
                }
            }
        }
    }

    private fun FolderFragmentBinding.setupNotesCount(
        notes: List<NoteItemModel>,
        labels: List<LabelItemModel>,
        filteringType: FilteringType,
        searchTerm: String,
    ) {
        val selectedLabels = labels.filterSelected()
        val filteredNotes = notes.filterByLabels(selectedLabels, filteringType)
            .filterBySearchTerm(searchTerm)
        val isFiltering = selectedLabels.isNotEmpty() || searchTerm.isNotBlank()
        val selectedNotes = notes.filter { it.isSelected }
        val notesCount = notes.count()
        val filteredNotesCount = filteredNotes.count()
        val selectedNotesCount = selectedNotes.count()
        when {
            // Filtering and selection.
            isFiltering && selectedNotes.isNotEmpty() -> {
                tvFolderNotesCount.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_selected_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                    selectedNotesCount,
                )
                tvFolderNotesCountRtl.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_selected_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                    selectedNotesCount,
                )
            }
            // Filtering only without selection.
            isFiltering && selectedNotes.isEmpty() -> {
                tvFolderNotesCount.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                )
                tvFolderNotesCountRtl.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                )
            }
            // Selection only without filtering.
            !isFiltering && selectedNotes.isNotEmpty() -> {
                tvFolderNotesCount.text = context?.quantityStringResource(
                    R.plurals.notes_selected_count,
                    notesCount,
                    notesCount,
                    selectedNotesCount,
                )
                tvFolderNotesCountRtl.text = context?.quantityStringResource(
                    R.plurals.notes_selected_count,
                    notesCount,
                    notesCount,
                    selectedNotesCount,
                )
            }
            // Without filtering or selection.
            else -> {
                tvFolderNotesCount.text =
                    context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvFolderNotesCountRtl.text =
                    context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
            }
        }
    }

    private fun FolderFragmentBinding.setupItemTouchHelper(
        layout: Layout,
        sortingType: NoteListSortingType
    ) {
        if (sortingType == NoteListSortingType.Manual) {
            itemTouchHelper = createCallbackInfo(layout)
                ?.let(::NoteItemTouchHelperCallback)
                ?.let(::ItemTouchHelper)
                ?.apply { attachToRecyclerView(rv) }
        } else {
            itemTouchHelper?.attachToRecyclerView(null)
        }
    }

    private fun FolderFragmentBinding.enableSearch() {
        tilSearch.isVisible = true
        tilSearch.postDelayed({ etSearch.requestFocus() }, DefaultAnimationDuration)
        activity?.showKeyboard(etSearch)
    }

    private fun FolderFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }

    private fun FolderFragmentBinding.createCallbackInfo(layout: Layout): NoteItemTouchHelperCallbackInfo? {
        val controller = epoxyController
        return if (controller != null) {
            object : NoteItemTouchHelperCallbackInfo {
                override val epoxyController: EpoxyController = controller
                override val dragFlags: Int = when (layout) {
                    Layout.Linear -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    Layout.Grid -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
                }

                override fun onItemSelected(item: NoteItem, binding: NoteItemBinding) {
                    viewModel.enableSelection()
                    if (item.model.isSelected)
                        viewModel.deselectNote(item.model.note.id)
                    else
                        viewModel.selectNote(item.model.note.id)
                }

                override fun onItemMoved(item: NoteItem, binding: NoteItemBinding) {
                    viewModel.disableSelection()
                    viewModel.enableDragging(item.model.note.id)
                    rv.forEach { rvView ->
                        val viewHolder = rv.findContainingViewHolder(rvView) as EpoxyViewHolder
                        val rvItem = viewHolder.model as? NoteItem
                        if (rvItem != null) viewModel.updateNotePosition(
                            rvItem.model.note,
                            viewHolder.bindingAdapterPosition
                        )
                    }
                }

                override fun onItemReleased(item: NoteItem, binding: NoteItemBinding) {
                    viewModel.disableDragging()
                }
            }
        } else {
            null
        }
    }

}
