package com.noto.app.note

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.EditText
import androidx.activity.addCallback
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ListUpdateCallback
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.NewNoteCursorPosition
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.label.labelItem
import com.noto.app.label.newLabelItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class NoteFragment : Fragment() {

    private val viewModel by sharedViewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId, args.body, args.labelsIds) }

    private val args by navArgs<NoteFragmentArgs>()

    private val undoRedoBackgroundDrawable by lazy {
        context?.drawableResource(R.drawable.generic_clickable_shape)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteFragmentBinding.inflate(inflater, container, false).withBinding {
//            setupMixedTransitions() // Temporally disabled due to focus isn't being set to the title.
            setupState()
            setupListeners()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.viewModelStore?.clear()
    }

    @OptIn(FlowPreview::class)
    private fun NoteFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        abl.bringToFront()
        tvWordCount.animationInterpolator = DefaultInterpolator()
        tvWordCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        viewModel.updateNoteAccessDate()

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .distinctUntilChangedBy { it.newNoteCursorPosition }
            .onEach { folder ->
                if (args.noteId == 0L) {
                    when (folder.newNoteCursorPosition) {
                        NewNoteCursorPosition.Body -> etNoteBody.post {
                            etNoteBody.requestFocus()
                            activity?.showKeyboard(etNoteBody)
                        }
                        NewNoteCursorPosition.Title -> etNoteTitle.post {
                            etNoteTitle.requestFocus()
                            activity?.showKeyboard(etNoteTitle)
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.note,
            viewModel.isRememberScrollingPosition,
            viewModel.isUndoOrRedo,
        ) { note, isRememberScrollingPosition, isUndoOrRedo ->
            createOrUpdateShortcut(note)
            val isTextNullOrBlank = etNoteTitle.text.isNullOrBlank() && etNoteBody.text.isNullOrBlank()
            if (isTextNullOrBlank || isUndoOrRedo) {
                setupNote(note, isRememberScrollingPosition)
                viewModel.resetIsUndoOrRedo()
            }
        }.launchIn(lifecycleScope)

        viewModel.note
            .distinctUntilChangedBy { note -> note.reminderDate }
            .onEach { note ->
                val reminderDrawable = context?.drawableResource(
                    if (note.reminderDate == null)
                        R.drawable.ic_round_notification_add_24
                    else
                        R.drawable.ic_round_edit_notifications_24
                )
                fab.setImageDrawable(reminderDrawable)
            }
            .launchIn(lifecycleScope)

        viewModel.note
            .distinctUntilChangedBy { note -> note.isValid }
            .onEach { note ->
                if (note.isValid)
                    enableBottomAppBarActions()
                else
                    disableBottomAppBarActions()
            }
            .launchIn(lifecycleScope)

        viewModel.font
            .onEach { font ->
                etNoteTitle.setSemiboldFont(font)
                etNoteBody.setMediumFont(font)
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.labels,
        ) { folder, labels ->
            rv.withModels {
                addModelBuildListener {
                    it.dispatchTo(NoteListUpdateCallback)
                }
                labels.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(folder.color)
                        onClickListener { _ ->
                            if (entry.value)
                                viewModel.unselectLabel(entry.key.id)
                            else
                                viewModel.selectLabel(entry.key.id)
                        }
                        onLongClickListener { _ ->
                            navController
                                ?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToLabelDialogFragment(args.folderId, entry.key.id))
                            true
                        }
                    }
                }
                newLabelItem {
                    id("new")
                    color(folder.color)
                    onClickListener { _ ->
                        navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToNewLabelDialogFragment(args.folderId))
                    }
                }
            }
        }.launchIn(lifecycleScope)

        etNoteBody.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .map { it.toString() }
            .onEach { body ->
                tvWordCount.text = context?.quantityStringResource(
                    R.plurals.words_count,
                    body.wordsCount,
                    body.wordsCount
                )
                tvWordCountRtl.text = context?.quantityStringResource(
                    R.plurals.words_count,
                    body.wordsCount,
                    body.wordsCount
                )
            }
            .launchIn(lifecycleScope)

        combine(
            etNoteTitle.textAsFlow(emitInitialText = true)
                .filterNotNull()
                .map { it.toString() },
            etNoteBody.textAsFlow(emitInitialText = true)
                .filterNotNull()
                .map { it.toString() },
        ) { title, body -> title to body }
            .debounce(DebounceTimeoutMillis)
            .onEach { (title, body) ->
                viewModel.createOrUpdateNote(title, body, trimContent = false)
                context?.updateAllWidgetsData()
                context?.updateNoteListWidgets()
            }
            .launchIn(lifecycleScope)

        etNoteTitle.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .debounce(DebounceTimeoutMillis)
            .map { it.toString() }
            .onEach { title -> viewModel.emitNewTitleOnly(title) }
            .launchIn(lifecycleScope)

        etNoteBody.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .debounce(DebounceTimeoutMillis)
            .map { it.toString() }
            .onEach { body -> viewModel.emitNewBodyOnly(body) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.titleHistory,
            etNoteTitle.textAsFlow(emitInitialText = true)
                .filterNotNull()
                .map { it.toString() },
            etNoteTitle.isFocusedAsFlow(),
        ) { _, title, isFocused -> isFocused to title }
            .debounce(DebounceTimeoutMillis)
            .onEach { (isFocused, title) ->
                if (isFocused) handleUndoRedo(viewModel.titleHistory.replayCache, title)
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.bodyHistory,
            etNoteBody.textAsFlow(emitInitialText = true)
                .filterNotNull()
                .map { it.toString() },
            etNoteBody.isFocusedAsFlow(),
        ) { _, body, isFocused -> isFocused to body }
            .debounce(DebounceTimeoutMillis)
            .onEach { (isFocused, body) ->
                if (isFocused) handleUndoRedo(viewModel.bodyHistory.replayCache, body)
            }
            .launchIn(lifecycleScope)

        nsv.scrollPositionAsFlow()
            .debounce(DebounceTimeoutMillis)
            .onEach { viewModel.updateNoteScrollingPosition(nsv.scrollY) }
            .launchIn(lifecycleScope)

        root.keyboardVisibilityAsFlow()
            .onEach { isVisible ->
                fab.isVisible = !isVisible
                bab.isVisible = !isVisible
                llToolbar.isVisible = isVisible
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteFragmentBinding.enableBottomAppBarActions() {
        fab.imageAlpha = EnabledAlpha
        fab.isEnabled = true
        bab.menu.forEach {
            it.isEnabled = true
            it.icon?.alpha = EnabledAlpha
        }
    }

    private fun NoteFragmentBinding.disableBottomAppBarActions() {
        fab.imageAlpha = DisabledAlpha
        fab.isEnabled = false
        bab.menu.forEach {
            it.isEnabled = false
            it.icon?.alpha = DisabledAlpha
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun NoteFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            navController
                ?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(args.folderId, viewModel.note.value.id))
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToMainFragment())
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToMainFragment())
        }

        val backCallback = {
            if (args.body != null)
                navController?.popBackStack(R.id.mainFragment, false)

            navController?.navigateUp()
            viewModel.createOrUpdateNote(
                etNoteTitle.text.toString(),
                etNoteBody.text.toString(),
                trimContent = true,
            )
            context?.updateAllWidgetsData()
            context?.updateNoteListWidgets()
            activity?.hideKeyboard(root)
        }

        activity?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner) { backCallback() }
            ?.isEnabled = true

        tb.setNavigationOnClickListener {
            backCallback()
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share_note -> {
                    launchShareNoteIntent(viewModel.note.value)
                    true
                }
                R.id.reading_mode -> {
                    setupFadeTransition()
                    navController
                        ?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToNoteReadingModeFragment(args.folderId, viewModel.note.value.id))
                    true
                }
                R.id.more -> {
                    navController?.navigateSafely(
                        NoteFragmentDirections.actionNoteFragmentToNoteDialogFragment(
                            args.folderId,
                            viewModel.note.value.id,
                            R.id.folderFragment
                        )
                    )
                    true
                }
                else -> false
            }
        }

        ibUndo.setOnClickListener {
            when {
                etNoteTitle.isFocused -> viewModel.undoTitle()
                etNoteBody.isFocused -> viewModel.undoBody()
            }
        }

        ibRedo.setOnClickListener {
            when {
                etNoteTitle.isFocused -> viewModel.redoTitle()
                etNoteBody.isFocused -> viewModel.redoBody()
            }
        }

        ibUndoHistory.setOnClickListener {
            when {
                etNoteTitle.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = true,
                        isTitle = true
                    )
                )
                etNoteBody.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = true,
                        isTitle = false
                    )
                )
            }
        }

        ibRedoHistory.setOnClickListener {
            when {
                etNoteTitle.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = false,
                        isTitle = true
                    )
                )
                etNoteBody.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = false,
                        isTitle = false
                    )
                )
            }
        }

        etNoteTitle.setOnSwipeGestureListener(
            onSwipeLeft = { viewModel.undoTitle() },
            onSwipeRight = { viewModel.redoTitle() },
            threshold = 0F,
        )

        etNoteBody.setOnSwipeGestureListener(
            onSwipeLeft = { viewModel.undoBody() },
            onSwipeRight = { viewModel.redoBody() },
            threshold = 0F,
        )

        val nsvClickListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                etNoteBody.requestFocus()
                etNoteBody.showKeyboardUsingImm()
                return super.onSingleTapUp(e)
            }
        }
        val gestureDetector = GestureDetector(requireContext(), nsvClickListener)
        nsv.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            v.performClick()
        }

        if (isCurrentLocaleArabic()) {
            tvWordCount.isVisible = false
            tvWordCountRtl.isVisible = true
            tvCreatedAt.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
            tvAccessedAt.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        } else {
            tvWordCount.isVisible = true
            tvWordCountRtl.isVisible = false
            tvCreatedAt.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
            tvAccessedAt.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
        }
    }

    private fun NoteFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            val highlightColor = color.withDefaultAlpha(alpha = if (folder.color == NotoColor.Black) 32 else 128)
            tvFolderTitle.text = folder.getTitle(context)
            tvFolderTitle.setTextColor(color)
            tb.navigationIcon?.mutate()?.setTint(color)
            fab.backgroundTintList = colorStateList
            etNoteTitle.setLinkTextColor(color)
            etNoteBody.setLinkTextColor(color)
            etNoteTitle.highlightColor = highlightColor
            etNoteBody.highlightColor = highlightColor
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                etNoteTitle.textCursorDrawable?.mutate()?.setTint(color)
                etNoteBody.textCursorDrawable?.mutate()?.setTint(color)
            }
        }
    }

    private fun NoteFragmentBinding.setupNote(note: Note, isRememberScrollingPosition: Boolean) {
        etNoteTitle.setText(note.title)
        etNoteBody.setText(note.body)
        etNoteTitle.setSelection(note.title.length)
        etNoteBody.setSelection(note.body.length)
        etNoteTitle.movementMethod = LinkMovementMethod.getInstance()
        etNoteBody.movementMethod = LinkMovementMethod.getInstance()
        tvWordCount.text = context?.quantityStringResource(R.plurals.words_count, note.body.wordsCount, note.body.wordsCount)
        tvWordCountRtl.text = context?.quantityStringResource(R.plurals.words_count, note.body.wordsCount, note.body.wordsCount)
        context?.let { context ->
            tvCreatedAt.text = context.stringResource(R.string.created, note.creationDate.format(context))
            tvAccessedAt.text = context.stringResource(R.string.accessed, note.accessDate.format(context))
        }
        nsv.post {
            if (isRememberScrollingPosition) {
                nsv.smoothScrollTo(0, note.scrollingPosition)
            }
            if (args.scrollPosition != -1) {
                nsv.smoothScrollTo(0, args.scrollPosition)
                when {
                    args.isTitleVisible && !args.isBodyVisible -> {
                        val index = etNoteTitle.getDisplayedTextIndex(args.scrollPosition)
                        etNoteTitle.showKeyboardAtIndex(index)
                    }
                    !args.isTitleVisible && args.isBodyVisible -> {
                        val index = etNoteBody.getDisplayedTextIndex(args.scrollPosition)
                        etNoteBody.showKeyboardAtIndex(index)
                    }
                    else -> when (viewModel.folder.value.newNoteCursorPosition) {
                        NewNoteCursorPosition.Body -> etNoteBody.showKeyboardAtIndex(0)
                        NewNoteCursorPosition.Title -> etNoteTitle.showKeyboardAtIndex(0)
                    }
                }
            }
        }
    }

    private fun createOrUpdateShortcut(note: Note) {
        if (note.id != 0L && note.isValid) {
            context?.let { context ->
                val intent = Intent(Constants.Intent.ActionOpenNote, null).apply {
                    putExtra(Constants.FolderId, note.folderId)
                    putExtra(Constants.NoteId, note.id)
                    component = context.enabledComponentName
                }
                val label = note.title.ifBlank { note.body }
                val shortcut = ShortcutInfoCompat.Builder(context, note.id.toString())
                    .setIntent(intent)
                    .setShortLabel(label)
                    .setLongLabel(label)
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_note))
                    .build()

                try {
                    ShortcutManagerCompat.getDynamicShortcuts(context).also { shortcuts ->
                        val maxShortcutsCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
                        if (shortcuts.count() == maxShortcutsCount) {
                            shortcuts.removeLastOrNull()
                            shortcuts.add(0, shortcut)
                            ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
                        } else {
                            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
                        }
                    }
                } catch (exception: Throwable) {
                    ShortcutManagerCompat.removeAllDynamicShortcuts(context)
                }
            }
        }
    }

    private fun EditText.showKeyboardAtIndex(index: Int) {
        setSelection(index)
        requestFocus()
        activity?.showKeyboard(this)
    }

    private fun NoteFragmentBinding.handleUndoRedo(replayCache: List<String>, currentText: String) {
        when {
            replayCache.none { it.isNotBlank() } -> {
                ibUndo.disable()
                ibRedo.disable()
                ibUndoHistory.disable()
                ibRedoHistory.disable()
            }
            replayCache.first() == currentText -> {
                ibUndo.disable()
                ibUndoHistory.disable()
                if (replayCache.size > 1) {
                    ibRedo.enable()
                    ibRedoHistory.enable()
                } else {
                    ibRedo.disable()
                    ibRedoHistory.disable()
                }
            }
            replayCache.last() == currentText -> {
                ibRedo.disable()
                ibRedoHistory.disable()
                if (replayCache.size > 1) {
                    ibUndo.enable()
                    ibUndoHistory.enable()
                } else {
                    ibUndo.disable()
                    ibUndoHistory.disable()
                }
            }
            else -> {
                ibUndo.enable()
                ibRedo.enable()
                ibUndoHistory.enable()
                ibRedoHistory.enable()
            }
        }
        ibUndo.background = context?.drawableResource(R.drawable.generic_clickable_shape)
        ibRedo.background = context?.drawableResource(R.drawable.generic_clickable_shape)
        ibUndoHistory.background = context?.drawableResource(R.drawable.generic_clickable_shape)
        ibRedoHistory.background = context?.drawableResource(R.drawable.generic_clickable_shape)
    }
}

private val NoteFragmentBinding.NoteListUpdateCallback
    get() = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) = if (rv.childCount == 1) rv.scrollToPosition(0) else Unit
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }