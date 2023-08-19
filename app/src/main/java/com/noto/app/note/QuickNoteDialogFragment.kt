package com.noto.app.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.QuickNoteDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.NewNoteCursorPosition
import com.noto.app.label.labelItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class QuickNoteDialogFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(folderId, 0L) }

    private val folderId by lazy { arguments?.getLong(Constants.FolderId) ?: Folder.GeneralFolderId }

    @OptIn(FlowPreview::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = QuickNoteDialogFragmentBinding.inflate(inflater, container, false).withBinding {
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        tvWordCount.animationInterpolator = DefaultInterpolator()
        tvWordCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toColorResourceId())
                    tvDialogTitle.setTextColor(color)
                    tvDialogTitle.text = folder.getTitle(context)
                }

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
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.labels,
        ) { folder, labels ->
            rv.withModels {
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
                        onLongClickListener { _ -> true }
                    }
                }
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

        viewModel.font
            .onEach { font ->
                etNoteTitle.setSemiboldFont(font)
                etNoteBody.setMediumFont(font)
            }
            .launchIn(lifecycleScope)

        etNoteBody.textAsFlow(emitInitialText = true)
            .filterNotNull()
            .map { it.toString() }
            .onEach { body ->
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
            }.launchIn(lifecycleScope)

        if (isCurrentLocaleArabic()) {
            tvWordCount.isVisible = false
            tvWordCountRtl.isVisible = true
        } else {
            tvWordCount.isVisible = true
            tvWordCountRtl.isVisible = false
        }

        dialog?.setOnDismissListener {
            viewModel.createOrUpdateNote(
                etNoteTitle.text.toString(),
                etNoteBody.text.toString(),
                trimContent = true,
            )
            setFragmentResult(Constants.QuickNote, bundleOf(Constants.NoteId to viewModel.note.value.id))
        }
    }

}