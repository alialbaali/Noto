package com.noto.app.note

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter


class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteFragmentBinding.inflate(inflater, container, false).withBinding {

        fab.setOnClickListener {
            findNavController()
                .navigate(NoteFragmentDirections.actionNotoFragmentToReminderDialogFragment(args.libraryId, args.noteId))
        }

        if (args.noteId == 0L) {
            etNoteBody.requestFocus()
            imm.showKeyboard()
        }

        val backCallback = {
            findNavController().navigateUp()
            viewModel.createOrUpdateNote(
                etNoteTitle.text.toString(),
                etNoteBody.text.toString(),
            )
            imm.hideKeyboard(etNoteBody.windowToken)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            backCallback()
        }.isEnabled = true

        tb.setNavigationOnClickListener {
            backCallback()
        }

        nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

        with(bab) {

            navigationIcon?.mutate()?.setTint(resources.colorResource(R.color.colorPrimary))

            setNavigationOnClickListener {
                findNavController().navigate(NoteFragmentDirections.actionNotoFragmentToNotoDialogFragment(args.libraryId, args.noteId))
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.share_noto -> {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            val content = """
                                ${etNoteTitle.text}
                                
                                ${etNoteBody.text}
                            """.trimIndent()
                            putExtra(Intent.EXTRA_TEXT, content)
                        }
                        val chooser = Intent.createChooser(intent, getString(R.string.share_note))
                        startActivity(chooser)
                        true
                    }
                    R.id.archive_note -> {
                        menuItem.icon = resources.drawableResource(R.drawable.ic_outline_unarchive_24)
                        if (viewModel.note.value.isArchived) {
                            viewModel.toggleNoteIsArchived()
                            root.snackbar(getString(R.string.note_unarchived))
                        } else {
                            viewModel.toggleNoteIsArchived()
                            root.snackbar(getString(R.string.note_archived))
                        }
                        true
                    }
                    else -> false
                }
            }
        }

        val archiveMenuItem = bab.menu.findItem(R.id.archive_note)

        viewModel.note
            .filterNotNull()
            .onEach {
                etNoteTitle.setText(it.title)
                etNoteBody.setText(it.body)
                etNoteTitle.setSelection(it.title.length)
                etNoteBody.setSelection(it.body.length)

                if (it.isArchived) archiveMenuItem.icon = resources.drawableResource(R.drawable.ic_outline_unarchive_24)
                else archiveMenuItem.icon = resources.drawableResource(R.drawable.archive_arrow_down_outline)

                if (it.reminderDate == null) fab.setImageDrawable(resources.drawableResource(R.drawable.bell_plus_outline))
                else fab.setImageDrawable(resources.drawableResource(R.drawable.bell_ring_outline))

                val timeZone = TimeZone.currentSystemDefault()
                it.creationDate
                    .toLocalDateTime(timeZone)
                    .toJavaLocalDateTime()
                    .apply {
                        val currentDateTime = Clock.System
                            .now()
                            .toLocalDateTime(timeZone)
                            .toJavaLocalDateTime()

                        val dateFormat = if (year > currentDateTime.year) format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
                        else format(DateTimeFormatter.ofPattern("EEE, MMM d"))

                        tvCreatedAt.text = "${getString(R.string.created_at)} $dateFormat"
                    }
            }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach {
                val color = resources.colorResource(it.color.toResource())

                tb.title = it.title
                tb.setTitleTextColor(color)
                tvCreatedAt.setTextColor(color)
                tb.navigationIcon?.mutate()?.setTint(color)
                fab.backgroundTintList = resources.colorStateResource(it.color.toResource())
            }
            .launchIn(lifecycleScope)
    }
}