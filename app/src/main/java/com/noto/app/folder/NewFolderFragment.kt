package com.noto.app.folder

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.databinding.NewFolderFragmentBinding
import com.noto.app.domain.model.*
import com.noto.app.main.SelectFolderDialogFragment
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewFolderFragment : Fragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NewFolderFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NewFolderFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    private fun NewFolderFragmentBinding.setupState() {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.clipToOutline = true
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        tvParentFolder.isVisible = args.folderId != Folder.GeneralFolderId
        tvParentFolderOption.isVisible = args.folderId != Folder.GeneralFolderId

        when (args.folderId) {
            0L -> {
                tb.title = context?.stringResource(R.string.new_folder)
                et.requestFocus()
                activity?.showKeyboard(root)
                btnCreate.text = context?.stringResource(R.string.create_folder)
            }
            Folder.GeneralFolderId -> {
                tb.title = context?.stringResource(R.string.edit_folder)
                til.isVisible = false
                tvFolderTitle.isVisible = false
                btnCreate.text = context?.stringResource(R.string.update_folder)
            }
            else -> {
                tb.title = context?.stringResource(R.string.edit_folder)
                btnCreate.text = context?.stringResource(R.string.update_folder)
            }
        }

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .launchIn(lifecycleScope)

        viewModel.parentFolder
            .onEach { parentFolder -> setupParentFolder(parentFolder) }
            .launchIn(lifecycleScope)

        viewModel.notoColors
            .onEach { pairs -> setupNotoColors(pairs) }
            .launchIn(lifecycleScope)
    }

    private fun NewFolderFragmentBinding.setupListeners() {
        tb.setOnClickListener {
            nsv.smoothScrollTo(0, 0)
        }

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        tvParentFolderOption.setOnClickListener {
            context?.let { context ->
                SelectFolderDialogFragment { folderId, _ -> viewModel.setParentFolder(folderId) }.apply {
                    arguments = bundleOf(
                        Constants.FilteredFolderIds to longArrayOf(Folder.GeneralFolderId, args.folderId),
                        Constants.SelectedFolderId to (viewModel.parentFolder.value?.id ?: 0L),
                        Constants.IsNoneEnabled to true,
                        Constants.Title to context.stringResource(R.string.parent_folder),
                    )
                }.show(parentFragmentManager, null)
            }
        }

        btnCreate.setOnClickListener {
            val title = et.text.toString()
            if (title.isBlank()) {
                til.isErrorEnabled = true
                context?.let { context -> til.error = context.stringResource(R.string.empty_title) }
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
                val openNotesIn = tlOpenNotesIn.selectedTabPosition.let {
                    when (it) {
                        0 -> OpenNotesIn.Editor
                        else -> OpenNotesIn.ReadingMode
                    }
                }
                activity?.hideKeyboard(root)
                updatePinnedShortcut(title)
                viewModel.createOrUpdateFolder(
                    title,
                    layout,
                    sNotePreviewSize.value.toInt(),
                    cursorPosition,
                    openNotesIn,
                    swShowNoteCreationDate.isChecked,
                    onCreateFolder = { folderId ->
                        navController?.navigateSafely(NewFolderFragmentDirections.actionNewFolderFragmentToFolderFragment(folderId))
                    }
                ).invokeOnCompletion {
                    context?.updateAllWidgetsData()
                    context?.updateFolderListWidgets()
                    context?.updateNoteListWidgets()
                    if (args.folderId != 0L)
                        navController?.navigateUp()
                }
            }
        }
    }

    private fun NewFolderFragmentBinding.setupFolder(folder: Folder) {
        rv.smoothScrollToPosition(folder.color.ordinal)
        val layoutTab = when (folder.layout) {
            Layout.Linear -> tlFolderLayout.getTabAt(0)
            Layout.Grid -> tlFolderLayout.getTabAt(1)
        }
        val cursorPositionTab = when (folder.newNoteCursorPosition) {
            NewNoteCursorPosition.Body -> tlNewNoteCursorPosition.getTabAt(0)
            NewNoteCursorPosition.Title -> tlNewNoteCursorPosition.getTabAt(1)
        }
        val openNotesInTab = when (folder.openNotesIn) {
            OpenNotesIn.Editor -> tlOpenNotesIn.getTabAt(0)
            OpenNotesIn.ReadingMode -> tlOpenNotesIn.getTabAt(1)
        }
        tlFolderLayout.selectTab(layoutTab)
        tlNewNoteCursorPosition.selectTab(cursorPositionTab)
        tlOpenNotesIn.selectTab(openNotesInTab)
        swShowNoteCreationDate.isChecked = folder.isShowNoteCreationDate
        swShowNoteCreationDate.setupColors()
        sNotePreviewSize.value = folder.notePreviewSize.toFloat()
        context?.let { context ->
            et.setText(folder.getTitle(context))
            et.setSelection(folder.getTitle(context).length)
            if (folder.id != 0L) {
                val color = context.colorResource(folder.color.toColorResourceId())
                tb.setTitleTextColor(color)
                tb.setNavigationIconTint(color)
                btnCreate.setBackgroundColor(color)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    btnCreate.outlineAmbientShadowColor = color
                    btnCreate.outlineSpotShadowColor = color
                }
            }
        }
    }

    private fun NewFolderFragmentBinding.setupParentFolder(parentFolder: Folder?) {
        context?.let { context ->
            if (parentFolder != null) {
                tvParentFolderOption.text = parentFolder.getTitle(context)
                tvParentFolderOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_folder_24, 0, 0, 0)
            } else {
                tvParentFolderOption.text = context.stringResource(R.string.none)
                tvParentFolderOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_none_24, 0, 0, 0)
            }
        }
    }

    private fun NewFolderFragmentBinding.setupNotoColors(pairs: List<Pair<NotoColor, Boolean>>) {
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

    private fun NewFolderFragmentBinding.updatePinnedShortcut(title: String) {
        val folder = viewModel.folder.value.copy(
            title = title,
            color = viewModel.notoColors.value.first { it.second }.first
        )
        context?.let { context ->
            ShortcutManagerCompat.updateShortcuts(context, listOf(context.createPinnedShortcut(folder)))
        }
    }
}