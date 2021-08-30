package com.noto.app.note

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId, args.body) }

    private val args by navArgs<NoteFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun NoteFragmentBinding.setupState() {

        if (args.noteId == 0L) {
            etNoteBody.requestFocus()
            requireActivity().showKeyboard(root)
        }

        nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
        val archiveMenuItem = bab.menu.findItem(R.id.archive_note)

        viewModel.state
            .onEach { state ->
                setupLibrary(state.library)
                setupNote(state.note, state.font, archiveMenuItem)
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            findNavController()
                .navigate(NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(args.libraryId, args.noteId))
        }

        val backCallback = {
            if (args.body != null)
                findNavController().popBackStack(R.id.mainFragment, false)

            findNavController().navigateUp()
            viewModel.createOrUpdateNote(
                etNoteTitle.text.toString(),
                etNoteBody.text.toString(),
            )
            requireActivity().hideKeyboard(root)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) { backCallback() }
            .isEnabled = true

        tb.setNavigationOnClickListener {
            backCallback()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(
                NoteFragmentDirections.actionNoteFragmentToNoteDialogFragment(
                    args.libraryId,
                    args.noteId,
                    R.id.libraryFragment
                )
            )
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share_noto -> {
                    launchShareNoteIntent(viewModel.state.value.note)
                    true
                }
                R.id.archive_note -> {
                    if (viewModel.state.value.note.isArchived) {
                        viewModel.toggleNoteIsArchived()
                        root.snackbar(getString(R.string.note_is_unarchived), anchorView = fab)
                    } else {
                        viewModel.toggleNoteIsArchived()
                        root.snackbar(getString(R.string.note_is_archived), anchorView = fab)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun NoteFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())

        tb.title = library.title
        tb.setTitleTextColor(color)
        tvCreatedAt.setTextColor(color)
        tvWordCount.setTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        fab.backgroundTintList = resources.colorStateResource(library.color.toResource())
        bab.menu.forEach { it.icon?.mutate()?.setTint(color) }
        bab.navigationIcon?.mutate()?.setTint(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fab.outlineAmbientShadowColor = color
            fab.outlineSpotShadowColor = color
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            etNoteTitle.textCursorDrawable?.mutate()?.setTint(color)
            etNoteBody.textCursorDrawable?.mutate()?.setTint(color)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun NoteFragmentBinding.setupNote(note: Note, font: Font, archiveMenuItem: MenuItem) {
        etNoteTitle.setText(note.title)
        etNoteBody.setText(note.body)
        etNoteTitle.setSelection(note.title.length)
        etNoteBody.setSelection(note.body.length)
        etNoteTitle.setBoldFont(font)
        etNoteBody.setSemiboldFont(font)
        tvCreatedAt.text = "${resources.stringResource(R.string.created)} ${note.formatCreationDate()}"
        tvWordCount.text = note.countWords(resources.stringResource(R.string.word), resources.stringResource(R.string.words))

        if (note.isArchived) archiveMenuItem.icon = resources.drawableResource(R.drawable.ic_round_unarchive_24)
        else archiveMenuItem.icon = resources.drawableResource(R.drawable.ic_round_archive_24)

        val color = viewModel.state.value.library.color.toResource()
        val resource = resources.colorResource(color)
        archiveMenuItem.icon?.mutate()?.setTint(resource)

        if (note.reminderDate == null) fab.setImageDrawable(resources.drawableResource(R.drawable.ic_round_notification_add_24))
        else fab.setImageDrawable(resources.drawableResource(R.drawable.ic_round_edit_notifications_24))

        setupShortcut(note)
    }

    private fun NoteFragmentBinding.setupShortcut(note: Note) {
        if (note.id != 0L && note.isValid()) {
            val intent = Intent(Intent.ACTION_EDIT, null, requireContext(), AppActivity::class.java).apply {
                putExtra(Constants.LibraryId, note.libraryId)
                putExtra(Constants.NoteId, note.id)
            }

            val label = note.title.ifBlank { note.body }

            val shortcut = ShortcutInfoCompat.Builder(requireContext(), note.id.toString())
                .setIntent(intent)
                .setShortLabel(label)
                .setLongLabel(label)
                .setIcon(IconCompat.createWithResource(requireContext(), R.mipmap.ic_note))
                .build()

            try {
                ShortcutManagerCompat.getDynamicShortcuts(requireContext()).also { shortcuts ->
                    val maxShortcutsCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(requireContext())
                    if (shortcuts.count() == maxShortcutsCount) {
                        shortcuts.removeLastOrNull()
                        shortcuts.add(0, shortcut)
                        ShortcutManagerCompat.setDynamicShortcuts(requireContext(), shortcuts)
                    } else {
                        ShortcutManagerCompat.pushDynamicShortcut(requireContext(), shortcut)
                    }
                }
            } catch (exception: Throwable) {
                ShortcutManagerCompat.removeAllDynamicShortcuts(requireContext())
            }
        }
    }
}