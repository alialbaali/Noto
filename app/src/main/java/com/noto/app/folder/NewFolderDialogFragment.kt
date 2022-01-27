package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewFolderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.NewNoteCursorPosition
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewFolderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NewFolderDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NewFolderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupState(baseDialogFragment)
            setupListeners()
        }

    private fun NewFolderDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            when (args.folderId) {
                0L -> {
                    tvDialogTitle.text = context.stringResource(R.string.new_folder)
                }
                else -> {
                    tvDialogTitle.text = context.stringResource(R.string.edit_folder)
                    btnCreate.text = context.stringResource(R.string.done)
                }
            }
        }
    }

    private fun NewFolderDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.clipToOutline = true
        rv.itemAnimator = HorizontalListItemAnimator()
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        if (args.folderId == 0L) {
            et.requestFocus()
            activity?.showKeyboard(root)
        }

        if (args.folderId == Folder.GeneralFolderId) {
            til.isVisible = false
            tvFolderTitle.isVisible = false
        }

        viewModel.folder
            .onEach { folder -> setupFolder(folder, baseDialogFragment) }
            .launchIn(lifecycleScope)

        viewModel.notoColors
            .onEach { pairs -> setupNotoColors(pairs) }
            .launchIn(lifecycleScope)
    }

    private fun NewFolderDialogFragmentBinding.setupListeners() {
        btnCreate.setOnClickListener {
            val title = et.text.toString()
            if (title.isBlank()) {
                til.isErrorEnabled = true
                context?.let { context ->
                    til.error = context.stringResource(R.string.empty_title)
                }
            } else {
                val layout = tlFolderLayout.selectedTabPosition.let {
                    when (it) {
                        0 -> Layout.Linear
                        else -> Layout.Grid
                    }
                }
                val cursorPosition = tlNewNoteCursorPosition.selectedTabPosition.let {
                    when (it) {
                        0 -> NewNoteCursorPosition.Body
                        else -> NewNoteCursorPosition.Title
                    }
                }
                activity?.hideKeyboard(root)
                updatePinnedShortcut(title)
                viewModel.createOrUpdateFolder(
                    title,
                    layout,
                    sNotePreviewSize.value.toInt(),
                    cursorPosition,
                    swShowNoteCreationDate.isChecked,
                ).invokeOnCompletion {
                    context?.updateAllWidgetsData()
                    context?.updateFolderListWidgets()
                    context?.updateNoteListWidgets(viewModel.folder.value.id)
                    dismiss()
                }
            }
        }
    }

    private fun NewFolderDialogFragmentBinding.setupFolder(folder: Folder, baseDialogFragment: BaseDialogFragmentBinding) {
        rv.smoothScrollToPosition(folder.color.ordinal)
        val layoutTab = when (folder.layout) {
            Layout.Linear -> tlFolderLayout.getTabAt(0)
            Layout.Grid -> tlFolderLayout.getTabAt(1)
        }
        val cursorPositionTab = when (folder.newNoteCursorPosition) {
            NewNoteCursorPosition.Body -> tlNewNoteCursorPosition.getTabAt(0)
            NewNoteCursorPosition.Title -> tlNewNoteCursorPosition.getTabAt(1)
        }
        tlFolderLayout.selectTab(layoutTab)
        tlNewNoteCursorPosition.selectTab(cursorPositionTab)
        swShowNoteCreationDate.isChecked = folder.isShowNoteCreationDate
        context?.let { context ->
            et.setText(folder.getTitle(context))
            et.setSelection(folder.getTitle(context).length)
            if (folder.id != 0L) {
                val color = context.colorResource(folder.color.toResource())
                val colorStateList = color.toColorStateList()
                baseDialogFragment.tvDialogTitle.setTextColor(color)
                baseDialogFragment.vHead.background?.mutate()?.setTint(color)
                tlFolderLayout.setSelectedTabIndicatorColor(color)
                tlNewNoteCursorPosition.setSelectedTabIndicatorColor(color)
                sNotePreviewSize.value = folder.notePreviewSize.toFloat()
                tlFolderLayout.tabRippleColor = colorStateList
                tlNewNoteCursorPosition.tabRippleColor = colorStateList
                sNotePreviewSize.trackActiveTintList = colorStateList
                sNotePreviewSize.thumbTintList = colorStateList
                sNotePreviewSize.tickInactiveTintList = colorStateList
                swShowNoteCreationDate.setupColors(thumbCheckedColor = color, trackCheckedColor = color)
            } else {
                swShowNoteCreationDate.setupColors()
            }
        }
    }

    private fun NewFolderDialogFragmentBinding.setupNotoColors(pairs: List<Pair<NotoColor, Boolean>>) {
        rv.withModels {
            pairs.forEach { pair ->
                notoColorItem {
                    id(pair.first.ordinal)
                    notoColor(pair.first)
                    isChecked(pair.second)
                    onClickListener { _ ->
                        viewModel.selectNotoColor(pair.first)
                    }
                }
            }
        }
    }

    private fun NewFolderDialogFragmentBinding.updatePinnedShortcut(title: String) {
        val folder = viewModel.folder.value.copy(
            title = title,
            color = viewModel.notoColors.value.first { it.second }.first
        )
        context?.let { context ->
            ShortcutManagerCompat.updateShortcuts(context, listOf(context.createPinnedShortcut(folder)))
        }
    }
}