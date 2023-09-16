package com.noto.app.note

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId, args.body, args.labelsIds) }

    private val args by navArgs<NoteFragmentArgs>()

    private val noteBodyOnFocusCompositeListener = OnFocusChangedCompositeListener()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    @Suppress("UNCHECKED_CAST")
    @OptIn(FlowPreview::class)
    private fun NoteFragmentBinding.setupState() {
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        abl.bringToFront()
        tvWordCount.animationInterpolator = DefaultInterpolator()
        tvWordCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        viewModel.updateNoteAccessDate()
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

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
            .distinctUntilChangedBy { it.reminderDate }
            .onEach { note ->
                llReminder.isVisible = note.reminderDate != null
                context?.let { context ->
                    tvReminder.text = note.reminderDate?.format(context)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.note
            .distinctUntilChangedBy { note -> note.reminderDate }
            .onEach { note ->
                val reminderDrawable = if (note.reminderDate == null)
                    R.drawable.ic_round_notification_add_24
                else
                    R.drawable.ic_round_edit_notifications_24
                bab.menu?.findItem(R.id.add_reminder)?.setIcon(reminderDrawable)
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

        combine(
            etNoteBody.textAsFlow(emitInitialText = true)
                .filterNotNull()
                .map { it.toString() },
            etNoteBody.textSelectionAsFlow(),
            viewModel.findInNoteIndices,
            viewModel.isFindInNoteEnabled,
        ) { body, selectedText, findInNoteIndices, isFindInNoteEnabled ->
            if (!selectedText.isNullOrBlank()) {
                tvWordCount.text = context?.quantityStringResource(
                    R.plurals.words_selected_count,
                    body.wordsCount,
                    body.wordsCount,
                    selectedText.wordsCount,
                )
                tvWordCountRtl.text = context?.quantityStringResource(
                    R.plurals.words_selected_count,
                    body.wordsCount,
                    body.wordsCount,
                    selectedText.wordsCount,
                )
            } else if (isFindInNoteEnabled) {
                tvWordCount.text = context?.quantityStringResource(
                    R.plurals.words_found_count,
                    body.wordsCount,
                    body.wordsCount,
                    findInNoteIndices.toList().indexOfFirst { it.second }.plus(1),
                    findInNoteIndices.count(),
                )
                tvWordCountRtl.text = context?.quantityStringResource(
                    R.plurals.words_found_count,
                    body.wordsCount,
                    body.wordsCount,
                    findInNoteIndices.toList().indexOfFirst { it.second }.plus(1),
                    findInNoteIndices.count(),
                )
            } else {
                tvWordCount.text = context?.quantityStringResource(
                    R.plurals.words_count,
                    body.wordsCount,
                    body.wordsCount,
                )
                tvWordCountRtl.text = context?.quantityStringResource(
                    R.plurals.words_count,
                    body.wordsCount,
                    body.wordsCount,
                )
            }
        }.launchIn(lifecycleScope)

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

        combine(
            viewModel.isTrackingTitleCursorPosition,
            etNoteTitle.cursorPositionAsFlow(),
        ) { isTracking, cursorPosition ->
            if (isTracking) {
                viewModel.setTitleCursorEndPosition(cursorPosition)
            } else {
                viewModel.setTitleCursorStartPosition(cursorPosition)
            }
        }.launchIn(lifecycleScope)

        combine(
            viewModel.isTrackingBodyCursorPosition,
            etNoteBody.cursorPositionAsFlow(),
        ) { isTracking, cursorPosition ->
            if (isTracking) {
                viewModel.setBodyCursorEndPosition(cursorPosition)
            } else {
                viewModel.setBodyCursorStartPosition(cursorPosition)
            }
        }.launchIn(lifecycleScope)

        etNoteTitle.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .onEach { viewModel.setIsTrackingTitleCursorPosition(true) }
            .debounce(DebounceTimeoutMillis)
            .map { it.toString() }
            .onEach { title -> viewModel.emitNewTitleOnly(title) }
            .launchIn(lifecycleScope)

        etNoteBody.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .onEach { viewModel.setIsTrackingBodyCursorPosition(true) }
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
            etNoteBody.isFocusedAsFlow(noteBodyOnFocusCompositeListener),
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

        combine(
            root.keyboardVisibilityAsFlow(),
            etFindInNote.isFocusedAsFlow(),
            viewModel.isFindInNoteEnabled,
        ) { isKeyboardVisible, isFindInNoteFocused, isFindInNoteEnabled ->
            if (isKeyboardVisible) {
                bab.performHide(true)
                bab.isVisible = false
                if (isFindInNoteEnabled) {
                    if (isFindInNoteFocused) {
                        llFindInNote.isVisible = true
                        babToolbar.performHide(true)
                        babToolbar.isVisible = false
                    } else {
                        llFindInNote.isVisible = false
                        babToolbar.performShow(true)
                        babToolbar.isVisible = true
                    }
                } else {
                    babToolbar.performShow(true)
                    babToolbar.isVisible = true
                }
            } else {
                llFindInNote.isVisible = isFindInNoteEnabled
                babToolbar.performHide(true)
                babToolbar.isVisible = false
                bab.performShow(true)
                bab.isVisible = true
            }
        }.launchIn(lifecycleScope)

        combine(
            viewModel.folder
                .distinctUntilChangedBy { it.color },
            viewModel.note
                .distinctUntilChangedBy { it.body },
            viewModel.isFindInNoteEnabled,
            viewModel.findInNoteTerm,
            viewModel.findInNoteIndices,
            root.keyboardVisibilityAsFlow()
                .debounce(DebounceTimeoutMillis),
            etNoteBody.isFocusedAsFlow(noteBodyOnFocusCompositeListener),
        ) { state ->
            val folder = state[0] as Folder
            val note = state[1] as Note
            val isEnabled = state[2] as Boolean
            val term = state[3] as String
            val indices = state[4] as Map<IntRange, Boolean>
            val isKeyboardVisible = state[5] as Boolean
            val isNoteBodyFocused = state[6] as Boolean
            if (isEnabled) {
                if (term.isNotBlank()) {
                    if (!isKeyboardVisible || !isNoteBodyFocused) {
                        val termIndices = indices.toList().firstOrNull { it.second }?.first
                        val currentIndex = etNoteBody.selectionStart.coerceIn(0, note.body.length)
                        etNoteBody.setHighlightedText(note.body, term, folder.color, termIndices)
                        etNoteBody.setSelection(currentIndex)
                        viewModel.setIsTextHighlighted(isHighlighted = true)
                    }
                }
            } else {
                if (viewModel.isTextHighlighted) {
                    val currentIndex = etNoteBody.selectionStart.coerceIn(0, note.body.length)
                    etNoteBody.setText(note.body)
                    etNoteBody.setSelection(currentIndex)
                    viewModel.setIsTextHighlighted(isHighlighted = false)
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.isFindInNoteEnabled
            .onEach { isEnabled -> if (isEnabled) enableFindInNote() else disableFindInNote() }
            .launchIn(lifecycleScope)

        combine(
            etFindInNote.textAsFlow()
                .onStart {
                    val isContinuousSearchEnabled = viewModel.continuousSearch.first()
                    if (!args.searchTerm.isNullOrBlank() && isContinuousSearchEnabled == true) {
                        viewModel.enableFindInNote()
                        emit(args.searchTerm)
                        etFindInNote.setText(args.searchTerm)
                        etFindInNote.setSelection(args.searchTerm?.length ?: 0)
                    }
                }
                .asSearchFlow(),
            viewModel.note
                .distinctUntilChangedBy { it.body },
        ) { term, note ->
            viewModel.setFindInNoteTerm(term, note.body)
        }.launchIn(lifecycleScope)

        combine(
            viewModel.findInNoteIndices
                .map { it.toList() },
            root.keyboardVisibilityAsFlow(),
            etNoteBody.isFocusedAsFlow(noteBodyOnFocusCompositeListener),
            etNoteBody.textAsFlow(emitInitialText = true),
        ) { indices, isKeyboardVisible, isNoteBodyFocused, _ ->
            val currentIndex = indices.indexOfFirst { it.second }
            val isPreviousEnabled = indices.getOrNull(currentIndex - 1) != null
            val isNextEnabled = indices.getOrNull(currentIndex + 1) != null
            if (isPreviousEnabled) ibPrevious.enable() else ibPrevious.disable()
            if (isNextEnabled) ibNext.enable() else ibNext.disable()

            if (!isKeyboardVisible || !isNoteBodyFocused) {
                indices.firstOrNull { it.second }
                    ?.first
                    ?.first
                    ?.let { etNoteBody.layout?.getLineForOffset(it) }
                    ?.let { etNoteBody.layout?.getLineTop(it) }
                    ?.let { scrollY -> nsv.smoothScrollTo(0, scrollY) }
            }
        }.launchIn(lifecycleScope)

        etNoteTitle.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .onEach {
                if (etNoteTitle.isFocused) {
                    etNoteTitle.post {
                        nsv.smoothScrollTo(0, etNoteTitle.currentLineScrollPosition, DefaultAnimationDuration.toInt())
                    }
                }
            }
            .launchIn(lifecycleScope)

        etNoteBody.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .onEach {
                if (etNoteBody.isFocused) {
                    etNoteBody.post {
                        nsv.smoothScrollTo(0, etNoteBody.currentLineScrollPosition, DefaultAnimationDuration.toInt())
                    }
                }
            }
            .launchIn(lifecycleScope)

        savedStateHandle?.getLiveData<String>(Constants.NoteTitle)
            ?.observe(viewLifecycleOwner) { title ->
                viewModel.setIsUndoOrRedo()
                viewModel.setNoteTitle(title)
            }

        savedStateHandle?.getLiveData<String>(Constants.NoteBody)
            ?.observe(viewLifecycleOwner) { body ->
                viewModel.setIsUndoOrRedo()
                viewModel.setNoteBody(body)
            }
    }

    private fun NoteFragmentBinding.enableBottomAppBarActions() {
        bab.menu.forEach {
            it.isEnabled = true
            it.icon?.alpha = EnabledAlpha
        }
    }

    private fun NoteFragmentBinding.disableBottomAppBarActions() {
        bab.menu.forEach {
            it.isEnabled = false
            it.icon?.alpha = DisabledAlpha
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun NoteFragmentBinding.setupListeners() {
        tb.setOnClickListener {
            nsv.smoothScrollTo(0, 0)
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToMainFragment())
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToMainFragment())
        }

        llReminder.setOnClickListener {
            navController?.navigateSafely(
                NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(
                    args.folderId,
                    viewModel.note.value.id
                )
            )
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
            ?.addCallback(viewLifecycleOwner) {
                if (viewModel.isFindInNoteEnabled.value) {
                    viewModel.disableFindInNote()
                } else {
                    backCallback()
                }
            }
            ?.isEnabled = true

        tb.setNavigationOnClickListener {
            backCallback()
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_reminder -> {
                    navController?.navigateSafely(
                        NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(
                            args.folderId,
                            viewModel.note.value.id
                        )
                    )
                    true
                }

                R.id.share_note -> {
                    launchShareNotesIntent(listOf(viewModel.note.value))
                    true
                }

                R.id.reading_mode -> {
                    setupFadeTransition()
                    navController
                        ?.navigateSafely(
                            NoteFragmentDirections.actionNoteFragmentToNotePagerFragment(
                                args.folderId,
                                viewModel.note.value.id,
                                selectedNoteIds = args.selectedNoteIds,
                            )
                        )
                    true
                }

                R.id.more -> {
                    navController?.navigateSafely(
                        NoteFragmentDirections.actionNoteFragmentToNoteDialogFragment(
                            args.folderId,
                            viewModel.note.value.id,
                            R.id.folderFragment,
                            selectedNoteIds = args.selectedNoteIds,
                        )
                    )
                    true
                }

                R.id.find_in_note -> {
                    if (viewModel.isFindInNoteEnabled.value)
                        viewModel.disableFindInNote()
                    else
                        viewModel.enableFindInNote()
                    true
                }

                else -> false
            }
        }

        ibUndo.setOnClickListener {
            when {
                etNoteTitle.isFocused -> {
                    val triple = viewModel.undoTitle()
                    val lastIndex = triple.third.lastIndex.takeUnless { it == -1 } ?: 0
                    val index = triple.second.coerceIn(0, lastIndex)
                    etNoteTitle.setSelection(index)
                }

                etNoteBody.isFocused -> {
                    val triple = viewModel.undoBody()
                    val lastIndex = triple.third.lastIndex.takeUnless { it == -1 } ?: 0
                    val index = triple.second.coerceIn(0, lastIndex)
                    etNoteBody.setSelection(index)
                }
            }
        }

        ibRedo.setOnClickListener {
            when {
                etNoteTitle.isFocused -> {
                    val triple = viewModel.redoTitle()
                    val lastIndex = triple.third.lastIndex.takeUnless { it == -1 } ?: 0
                    val index = triple.second.coerceIn(0, lastIndex)
                    etNoteTitle.setSelection(index)
                }

                etNoteBody.isFocused -> {
                    val triple = viewModel.redoBody()
                    val lastIndex = triple.third.lastIndex.takeUnless { it == -1 } ?: 0
                    val index = triple.second.coerceIn(0, lastIndex)
                    etNoteBody.setSelection(index)
                }
            }
        }

        ibUndoHistory.setOnClickListener {
            val currentTitleText = viewModel.note.value.title
            val currentBodyText = viewModel.note.value.body
            val titleContent = viewModel.titleHistory.replayCache.subListOld(currentTitleText)
            val bodyContent = viewModel.bodyHistory.replayCache.subListOld(currentBodyText)

            when {
                etNoteTitle.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = true,
                        isTitle = true,
                        currentTitleText = currentTitleText,
                        currentBodyText = currentBodyText,
                        startCursorIndices = titleContent
                            .map { it.first }
                            .toTypedArray()
                            .toIntArray(),
                        endCursorIndices = titleContent
                            .map { it.second }
                            .toTypedArray()
                            .toIntArray(),
                        content = titleContent
                            .map { it.third }
                            .toTypedArray(),
                    )
                )

                etNoteBody.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = true,
                        isTitle = false,
                        currentTitleText = currentTitleText,
                        currentBodyText = currentBodyText,
                        startCursorIndices = bodyContent
                            .map { it.first }
                            .toTypedArray()
                            .toIntArray(),
                        endCursorIndices = bodyContent
                            .map { it.second }
                            .toTypedArray()
                            .toIntArray(),
                        content = bodyContent
                            .map { it.third }
                            .toTypedArray(),
                    )
                )
            }
        }

        ibRedoHistory.setOnClickListener {
            val currentTitleText = viewModel.note.value.title
            val currentBodyText = viewModel.note.value.body
            val titleContent = viewModel.titleHistory.replayCache.subListNew(currentTitleText)
            val bodyContent = viewModel.bodyHistory.replayCache.subListNew(currentBodyText)

            when {
                etNoteTitle.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = false,
                        isTitle = true,
                        currentTitleText = currentTitleText,
                        currentBodyText = currentBodyText,
                        startCursorIndices = titleContent
                            .map { it.first }
                            .toTypedArray()
                            .toIntArray(),
                        endCursorIndices = titleContent
                            .map { it.second }
                            .toTypedArray()
                            .toIntArray(),
                        content = titleContent
                            .map { it.third }
                            .toTypedArray(),
                    )
                )

                etNoteBody.isFocused -> navController?.navigateSafely(
                    NoteFragmentDirections.actionNoteFragmentToUndoRedoDialogFragment(
                        args.folderId,
                        args.noteId,
                        isUndo = false,
                        isTitle = false,
                        currentTitleText = currentTitleText,
                        currentBodyText = currentBodyText,
                        startCursorIndices = bodyContent
                            .map { it.first }
                            .toTypedArray()
                            .toIntArray(),
                        endCursorIndices = bodyContent
                            .map { it.second }
                            .toTypedArray()
                            .toIntArray(),
                        content = bodyContent
                            .map { it.third }
                            .toTypedArray(),
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
            override fun onSingleTapUp(e: MotionEvent): Boolean {
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

        ibPrevious.setOnClickListener {
            etNoteTitle.clearFocus()
            etNoteBody.clearFocus()
            viewModel.selectPreviousFindInNoteIndex()
        }

        ibNext.setOnClickListener {
            etNoteTitle.clearFocus()
            etNoteBody.clearFocus()
            viewModel.selectNextFindInNoteIndex()
        }
    }

    private fun NoteFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toColorResourceId())
            val highlightColor = color.withDefaultAlpha(alpha = if (folder.color == NotoColor.Black) 32 else 128)
            val backgroundColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            tvFolderTitle.text = folder.getTitle(context)
            tvFolderTitle.setTextColor(color)
            tb.navigationIcon?.mutate()?.setTint(color)
            etNoteTitle.setLinkTextColor(color)
            etNoteBody.setLinkTextColor(color)
            etNoteTitle.highlightColor = highlightColor
            etNoteBody.highlightColor = highlightColor
            llReminder.background?.mutate()?.setTint(color)
            llReminder.background?.setRippleColor(backgroundColor.toColorStateList())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                etNoteTitle.textCursorDrawable?.mutate()?.setTint(color)
                etNoteBody.textCursorDrawable?.mutate()?.setTint(color)
                etFindInNote.textCursorDrawable?.mutate()?.setTint(color)
            }
        }
    }

    private fun NoteFragmentBinding.setupNote(note: Note, isRememberScrollingPosition: Boolean) {
        etNoteTitle.setText(note.title)
        etNoteBody.setText(note.body)
        etNoteTitle.setSelection(note.title.length)
        etNoteBody.setSelection(note.body.length)
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

    private fun NoteFragmentBinding.handleUndoRedo(replayCache: List<Triple<Int, Int, String>>, currentText: String) {
        val value = replayCache.lastOrNull() ?: Triple(0, 0, "")
        val isLastCharWhiteSpace = if (value.third.isNotBlank()) {
            val second = value.second
            val first = value.first.coerceIn(0, second)
            value.third.substring(first, second).lastOrNull()?.isWhitespace() == true
        } else {
            true
        }

        when {
            replayCache.all { it.third.isBlank() } -> {
                ibUndo.disable()
                ibRedo.disable()
                ibUndoHistory.disable()
                ibRedoHistory.disable()
            }

            replayCache.first().third == currentText -> {
                ibUndo.disable()
                ibUndoHistory.disable()
                if (replayCache.size > 1) {
                    ibRedo.enable()
                    if (isLastCharWhiteSpace) {
                        ibRedoHistory.enable()
                    } else {
                        ibRedoHistory.disable()
                    }
                } else {
                    ibRedo.disable()
                    ibRedoHistory.disable()
                }
            }

            replayCache.last().third == currentText -> {
                ibRedo.disable()
                ibRedoHistory.disable()
                if (replayCache.size > 1) {
                    ibUndo.enable()
                    if (isLastCharWhiteSpace) {
                        ibUndoHistory.enable()
                    } else {
                        ibUndoHistory.disable()
                    }
                } else {
                    ibUndo.disable()
                    ibUndoHistory.disable()
                }
            }

            else -> {
                ibUndo.enable()
                ibRedo.enable()
                if (isLastCharWhiteSpace) {
                    ibUndoHistory.enable()
                    ibRedoHistory.enable()
                } else {
                    ibUndoHistory.disable()
                    ibRedoHistory.disable()
                }
            }
        }
    }

    private fun NoteFragmentBinding.enableFindInNote() {
        llFindInNote.isVisible = true
        if (args.searchTerm.isNullOrBlank()) {
            llFindInNote.postDelayed({ etFindInNote.requestFocus() }, DefaultAnimationDuration)
            activity?.showKeyboard(etFindInNote)
        } else {
            activity?.hideKeyboard(etFindInNote)
        }
    }

    private fun NoteFragmentBinding.disableFindInNote() {
        llFindInNote.isVisible = false
        activity?.hideKeyboard(etFindInNote)
        etFindInNote.text = null
    }

    private fun List<Triple<Int, Int, String>>.subListOld(currentText: String): List<Triple<Int, Int, String>> {
        val indexOfCurrentText = indexOfFirst { it.third == currentText }
        return subList(0, indexOfCurrentText + 1)
    }

    private fun List<Triple<Int, Int, String>>.subListNew(currentText: String): List<Triple<Int, Int, String>> {
        val indexOfCurrentText = indexOfFirst { it.third == currentText }
        return subList(indexOfCurrentText, size)
    }
}

private val NoteFragmentBinding.NoteListUpdateCallback
    get() = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) = if (rv.childCount == 1) rv.scrollToPosition(0) else Unit
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }