package com.noto.app.note

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.R
import com.noto.app.databinding.NoteReadingModeFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.label.labelItem
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val RightScrollDirection = 1
private const val LeftScrollDirection = -1

class NoteReadingModeFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(folderId, noteId) }

    private val folderId by lazy { arguments?.getLong(Constants.FolderId) }

    private val noteId by lazy { arguments?.getLong(Constants.NoteId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReadingModeFragmentBinding.inflate(inflater, container, false).withBinding {
            setupFadeTransition()
            setupState()
            setupListeners()
        }

    private fun NoteReadingModeFragmentBinding.setupListeners() {
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle
        nsv.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                savedStateHandle?.set(Constants.ScrollPosition, scrollY)
                savedStateHandle?.set(Constants.IsTitleVisible, tvNoteTitle.isLayoutVisible(root))
                savedStateHandle?.set(Constants.IsBodyVisible, tvNoteBody.isLayoutVisible(root))
            }
        )

        rv.addOnItemTouchListener(RecyclerViewItemTouchListener)
    }

    private fun NoteReadingModeFragmentBinding.setupState() {
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        viewModel.updateNoteAccessDate()

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.note,
            viewModel.font,
        ) { note, font -> setupNote(note, font) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.labels,
        ) { folder, labels ->
            rv.withModels {
                labels.filterValues { it }.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(folder.color)
                        onClickListener { _ -> }
                        onLongClickListener { _ -> false }
                    }
                }
            }
        }.launchIn(lifecycleScope)

        navController?.getBackStackEntry(R.id.notePagerFragment)?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) { if (isVisible) nsv.smoothScrollTo(0, 0) }
    }

    private fun NoteReadingModeFragmentBinding.setupNote(note: Note, font: Font) {
        tvNoteTitle.text = note.title
        tvNoteBody.text = note.body
        tvNoteTitle.isVisible = note.title.isNotBlank()
        tvNoteBody.isVisible = note.body.isNotBlank()
        tvNoteTitle.setSemiboldFont(font)
        tvNoteBody.setMediumFont(font)
    }

    private fun NoteReadingModeFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toColorResourceId())
            val highlightColor = color.withDefaultAlpha(alpha = if (folder.color == NotoColor.Black) 32 else 128)
            tvNoteTitle.setLinkTextColor(color)
            tvNoteBody.setLinkTextColor(color)
            tvNoteTitle.highlightColor = highlightColor
            tvNoteBody.highlightColor = highlightColor
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                nsv.verticalScrollbarThumbDrawable?.mutate()?.setTint(color)
            }
        }
    }
}

private val RecyclerViewItemTouchListener = object : RecyclerView.OnItemTouchListener {
    private var startX = 0f

    override fun onInterceptTouchEvent(
        recyclerView: RecyclerView,
        event: MotionEvent
    ): Boolean =
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startX = event.x
            MotionEvent.ACTION_MOVE -> {
                val isScrollingRight = event.x < startX
                val scrollItemsToRight = isScrollingRight && recyclerView.canScrollRight
                val scrollItemsToLeft = !isScrollingRight && recyclerView.canScrollLeft
                val disallowIntercept = scrollItemsToRight || scrollItemsToLeft
                recyclerView.parent.requestDisallowInterceptTouchEvent(disallowIntercept)
            }

            MotionEvent.ACTION_UP -> startX = 0f
            else -> Unit
        }.let { false }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
}

private val RecyclerView.canScrollRight: Boolean
    get() = canScrollHorizontally(RightScrollDirection)

private val RecyclerView.canScrollLeft: Boolean
    get() = canScrollHorizontally(LeftScrollDirection)