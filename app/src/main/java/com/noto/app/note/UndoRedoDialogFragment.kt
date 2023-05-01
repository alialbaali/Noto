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
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.UndoRedoDialogFragmentBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UndoRedoDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

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
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()
        tb.tvDialogTitle.text = context?.stringResource(if (args.isUndo) R.string.undo_history else R.string.redo_history)

        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.ll.isSelected = isScrolling }
            .launchIn(lifecycleScope)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val colorResource = context.colorResource(folder.color.toResource())
                    tb.tvDialogTitle.setTextColor(colorResource)
                    tb.vHead.background?.mutate()?.setTint(colorResource)
                }

                val items = args.startCursorIndices
                    .zip(args.endCursorIndices)
                    .zip(args.content)
                    .map { Triple(it.first.first, it.first.second, it.second) }
                    .distinctBy { it.third }
                    .filter { it.third.isNotBlank() }
                    .filter {
                        if (it.third.isNotBlank()) {
                            val second = it.second
                            val first = it.first.coerceIn(0, second)
                            it.third.substring(first, second).lastOrNull()?.isWhitespace() == true
                        } else {
                            true
                        }
                    }

                if (args.isTitle) {
                    val currentText = args.currentTitleText
                    if (args.isUndo) {
                        setupItems(items, currentText, isTitle = true, folder.color)
                        rv.scrollToPosition(items.lastIndexSafely)
                    } else {
                        setupItems(items, currentText, isTitle = true, folder.color)
                        rv.scrollToPosition(0)
                    }
                } else {
                    val currentText = args.currentBodyText
                    if (args.isUndo) {
                        setupItems(items, currentText, isTitle = false, folder.color)
                        rv.scrollToPosition(items.lastIndexSafely)
                    } else {
                        setupItems(items, currentText, isTitle = false, folder.color)
                        rv.scrollToPosition(0)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun UndoRedoDialogFragmentBinding.setupItems(
        items: List<Triple<Int, Int, String>>,
        currentItem: String,
        isTitle: Boolean,
        color: NotoColor,
    ) {
        rv.withModels {
            items.forEachIndexed { index, item ->
                undoRedoItem {
                    id("${item.first} ${item.second} ${item.third}")
                    text(item.third)
                    index(index)
                    cursorStartPosition(item.first)
                    cursorEndPosition(item.second)
                    isSelected(item.third == currentItem)
                    color(color)
                    onClickListener { _ ->
                        val key = if (isTitle) Constants.NoteTitle else Constants.NoteBody
                        navController?.previousBackStackEntry?.savedStateHandle?.set(key, item.third)
                        dismiss()
                    }
                    onCopyClickListener { _ ->
                        val clipData = ClipData.newPlainText(args.currentTitleText, item.third)
                        clipboardManager?.setPrimaryClip(clipData)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            val stringId = R.string.text_copied
                            val drawableId = R.drawable.ic_round_copy_24
                            val anchorViewId = R.id.bab
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

    private val <T> List<T>.lastIndexSafely
        get() = lastIndex.takeUnless { it == -1 } ?: 0
}