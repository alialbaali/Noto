package com.noto.app.note

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.UndoRedoDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class UndoRedoDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<UndoRedoDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    private val etNoteTitle by lazy { parentFragment?.view?.findViewById<EditText>(R.id.et_note_title) }

    private val etNoteBody by lazy { parentFragment?.view?.findViewById<EditText>(R.id.et_note_body) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = UndoRedoDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
    }

    private fun UndoRedoDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()
        tb.tvDialogTitle.text = context?.stringResource(if (args.isUndo) R.string.undo_history else R.string.redo_history)

        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.ll.isSelected = isScrolling }
            .launchIn(lifecycleScope)

        if (args.isTitle) {
            val currentText = viewModel.note.value.title
            if (args.isUndo) {
                val items = viewModel.titleHistory.replayCache.subListOld(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText, isTitle = true)
                rv.smoothScrollToPosition(items.lastIndexSafely)
            } else {
                val items = viewModel.titleHistory.replayCache.subListNew(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText, isTitle = true)
                rv.smoothScrollToPosition(0)
            }
        } else {
            val currentText = viewModel.note.value.body
            if (args.isUndo) {
                val items = viewModel.bodyHistory.replayCache.subListOld(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText, isTitle = false)
                rv.smoothScrollToPosition(items.lastIndexSafely)
            } else {
                val items = viewModel.bodyHistory.replayCache.subListNew(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText, isTitle = false)
                rv.smoothScrollToPosition(0)
            }
        }
    }

    private fun UndoRedoDialogFragmentBinding.setupItems(items: List<String>, currentItem: String, isTitle: Boolean) {
        rv.withModels {
            items.forEach { item ->
                undoRedoItem {
                    id(item)
                    text(item)
                    isSelected(item == currentItem)
                    onClickListener { _ ->
                        viewModel.setIsUndoOrRedo()
                        if (isTitle) viewModel.setNoteTitle(item) else viewModel.setNoteBody(item)
                        dismiss()
                    }
                    onCopyClickListener { _ ->
                        val clipData = ClipData.newPlainText(viewModel.note.value.title, item)
                        clipboardManager?.setPrimaryClip(clipData)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            val stringId = R.string.text_copied
                            val drawableId = R.drawable.ic_round_copy_24
                            val anchorViewId = R.id.fab
                            val folderColor = viewModel.folder.value.color
                            context?.let { context ->
                                parentFragment?.view?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, folderColor)
                            }
                        }
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (args.isTitle) {
            etNoteTitle?.let { activity?.showKeyboard(it) }
        } else {
            etNoteBody?.let { activity?.showKeyboard(it) }
        }
    }

    private fun List<String>.subListOld(currentText: String): List<String> {
        val indexOfCurrentText = indexOf(currentText)
        return subList(0, indexOfCurrentText + 1)
    }

    private fun List<String>.subListNew(currentText: String): List<String> {
        val indexOfCurrentText = indexOf(currentText)
        return subList(indexOfCurrentText, size)
    }

    private val <T> List<T>.lastIndexSafely
        get() = lastIndex.takeUnless { it == -1 } ?: 0
}