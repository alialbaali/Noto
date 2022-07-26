package com.noto.app.note

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            if (args.isUndo) {
                val currentText = viewModel.note.value.title
                val items = viewModel.titleHistory.replayCache.subListOld(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText) { title ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteTitle(title)
                    dismiss()
                }
                rv.smoothScrollToPosition(items.lastIndexSafely)
            } else {
                val currentText = viewModel.note.value.title
                val items = viewModel.titleHistory.replayCache.subListNew(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText) { title ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteTitle(title)
                    dismiss()
                }
                rv.smoothScrollToPosition(0)
            }
        } else {
            if (args.isUndo) {
                val currentText = viewModel.note.value.body
                val items = viewModel.bodyHistory.replayCache.subListOld(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText) { body ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteBody(body)
                    dismiss()
                }
                rv.smoothScrollToPosition(items.lastIndexSafely)
            } else {
                val currentText = viewModel.note.value.body
                val items = viewModel.bodyHistory.replayCache.subListNew(currentText).filter { it.isNotBlank() }
                setupItems(items, currentText) { body ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteBody(body)
                    dismiss()
                }
                rv.smoothScrollToPosition(0)
            }
        }
    }

    private fun UndoRedoDialogFragmentBinding.setupItems(items: List<String>, currentItem: String, onClick: (String) -> Unit) {
        rv.withModels {
            items.forEach { item ->
                undoRedoItem {
                    id(item)
                    text(item)
                    isSelected(item == currentItem)
                    onClickListener { _ -> onClick(item) }
                    onCopyClickListener { _ ->
                        val clipData = ClipData.newPlainText(viewModel.note.value.title, item)
                        clipboardManager?.setPrimaryClip(clipData)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            val stringId = R.string.text_copied
                            val drawableId = R.drawable.ic_round_copy_24
                            val anchorViewId = R.id.fab
                            val folderColor = viewModel.folder.value.color
                            parentFragment?.view?.snackbar(stringId, drawableId, anchorViewId, folderColor)
                        }
                        dismiss()
                    }
                }
            }
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