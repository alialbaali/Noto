package com.noto.app.note

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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val NoteId = "noto_id"
const val NoteTitle = "noto_title"
const val NoteBody = "noto_body"
const val NoteColor = "noto_color"
const val NoteIcon = "noto_icon"

class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteFragmentBinding.inflate(inflater, container, false).withBinding {

        fab.setOnClickListener {
            findNavController()
                .navigate(NoteFragmentDirections.actionNotoFragmentToReminderDialogFragment())
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
                isStarred = rbNoteStar.isChecked
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

            navigationIcon?.mutate()?.setTint(colorResource(R.color.colorPrimary))

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
                        menuItem.icon = drawableResource(R.drawable.ic_outline_unarchive_24)
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
            .onEach {
                etNoteTitle.setText(it.title)
                etNoteBody.setText(it.body)
                etNoteTitle.setSelection(it.title.length)
                etNoteBody.setSelection(it.body.length)
                rbNoteStar.isChecked = it.isStarred

                if (it.isArchived) archiveMenuItem.icon = drawableResource(R.drawable.ic_outline_unarchive_24)
                else archiveMenuItem.icon = drawableResource(R.drawable.archive_arrow_down_outline)

                if (it.reminderDate == null) fab.setImageDrawable(drawableResource(R.drawable.bell_plus_outline))
                else fab.setImageDrawable(drawableResource(R.drawable.bell_ring_outline))

                it.creationDate.apply {
                    val dateFormat = if (year > ZonedDateTime.now().year) format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
                    else format(DateTimeFormatter.ofPattern("EEE, MMM d"))

                    tvCreatedAt.text = "${getString(R.string.created_at)} ${dateFormat.uppercase()}"
                }
            }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach {
                val color = colorResource(it.color.toResource())

                tvLibraryTitle.text = it.title
                tvLibraryTitle.setTextColor(color)
                tvCreatedAt.setTextColor(color)
                tb.navigationIcon?.mutate()?.setTint(color)
                fab.backgroundTintList = colorStateResource(it.color.toResource())
            }
            .launchIn(lifecycleScope)

    }

}