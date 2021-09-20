package com.noto.app.note

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
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
import androidx.recyclerview.widget.ListUpdateCallback
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.label.labelItem
import com.noto.app.label.newLabelItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val DebounceTimeoutMillis = 250L

class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId, args.body) }

    private val args by navArgs<NoteFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    @OptIn(FlowPreview::class)
    private fun NoteFragmentBinding.setupState() {
        nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .distinctUntilChangedBy { it.isSetNewNoteCursorOnTitle }
            .onEach { library ->
                if (args.noteId == 0L) {
                    requireActivity().showKeyboard(root)
                    if (library.isSetNewNoteCursorOnTitle)
                        etNoteTitle.requestFocus()
                    else
                        etNoteBody.requestFocus()
                }
            }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach { note ->
                setupShortcut(note)
                tvWordCount.text = note.countWords(resources.stringResource(R.string.word), resources.stringResource(R.string.words))
                fab.setImageDrawable(
                    if (note.reminderDate == null)
                        resources.drawableResource(R.drawable.ic_round_notification_add_24)
                    else
                        resources.drawableResource(R.drawable.ic_round_edit_notifications_24)
                )
            }
            .distinctUntilChanged { _, _ -> etNoteTitle.text.isNotBlank() || etNoteBody.text.isNotBlank() }
            .combine(viewModel.font) { note, font -> setupNote(note, font) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.library,
            viewModel.labels,
        ) { library, labels ->
            rv.withModels {
                addModelBuildListener {
                    it.dispatchTo(NoteListUpdateCallback)
                }
                labels.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(library.color)
                        onClickListener { _ ->
                            if (entry.value)
                                viewModel.unselectLabel(entry.key.id)
                            else
                                viewModel.selectLabel(entry.key.id)
                        }
                        onLongClickListener { _ ->
                            findNavController()
                                .navigate(NoteFragmentDirections.actionNoteFragmentToLabelDialogFragment(args.libraryId, entry.key.id))
                            true
                        }
                    }
                }
                newLabelItem {
                    id("new")
                    color(library.color)
                    onClickListener { _ ->
                        findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToNewLabelDialogFragment(args.libraryId))
                    }
                }
            }
        }.launchIn(lifecycleScope)

        combine(
            etNoteTitle.textAsFlow()
                .filterNotNull(),
            etNoteBody.textAsFlow()
                .filterNotNull(),
        ) { title, body -> title to body }
            .debounce(DebounceTimeoutMillis)
            .onEach { (title, body) -> viewModel.createOrUpdateNote(title.toString(), body.toString()) }
            .launchIn(lifecycleScope)
    }

    private fun NoteFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            findNavController()
                .navigate(NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(args.libraryId, viewModel.note.value.id))
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
                    viewModel.note.value.id,
                    R.id.libraryFragment
                )
            )
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share_note -> {
                    launchShareNoteIntent(viewModel.note.value)
                    true
                }
                R.id.reading_mode -> {
                    findNavController()
                        .navigate(NoteFragmentDirections.actionNoteFragmentToNoteReadingModeFragment(args.libraryId, viewModel.note.value.id))
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
    private fun NoteFragmentBinding.setupNote(note: Note, font: Font) {
        etNoteTitle.setText(note.title)
        etNoteBody.setText(note.body)
        etNoteTitle.setSelection(note.title.length)
        etNoteBody.setSelection(note.body.length)
        etNoteTitle.setBoldFont(font)
        etNoteBody.setSemiboldFont(font)
        tvCreatedAt.text = "${resources.stringResource(R.string.created)} ${note.creationDate.format(requireContext())}"
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

    private val NoteFragmentBinding.NoteListUpdateCallback
        get() = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) = if (rv.childCount == 1) rv.scrollToPosition(0) else Unit
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
}