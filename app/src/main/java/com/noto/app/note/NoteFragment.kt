package com.noto.app.note

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val NOTO_ID = "noto_id"
const val NOTO_TITLE = "noto_title"
const val NOTO_BODY = "noto_body"
const val NOTO_COLOR = "noto_color"
const val NOTO_ICON = "noto_icon"

class NoteFragment : Fragment() {

    private val viewModel by sharedViewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteFragmentArgs>()

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

            viewModel.postNote(args.libraryId)

            etNotoBody.requestFocus()
//            etNotoBody.showKeyboard()

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().navigateUp()
                viewModel.createNote()
            }.isEnabled = true

            tb.setNavigationOnClickListener {
//                etNotoBody.hideKeyboard()
                findNavController().navigateUp()
                viewModel.createNote()
            }

        } else {

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().navigateUp()
                viewModel.updateNote()
            }.isEnabled = true


            tb.setNavigationOnClickListener {
//                etNotoBody.hideKeyboard()
                findNavController().navigateUp()
                viewModel.updateNote()
            }

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

                        val note = viewModel.note.value

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, note?.title)
                            putExtra(Intent.EXTRA_TEXT, note?.body)
                        }

                        val chooser = Intent.createChooser(intent, "${getString(R.string.share)} ${note?.title} to")

                        startActivity(chooser)

                        true
                    }

                    R.id.archive_note -> {

                        menuItem.icon = drawableResource(R.drawable.ic_outline_unarchive_24)

                        if (viewModel.note.value?.isArchived == true) {
                            viewModel.setNotoArchived(false)
                            root.snackbar(getString(R.string.note_unarchived))
                        } else {
                            viewModel.setNotoArchived(true)
                            root.snackbar(getString(R.string.note_archived))
                        }

                        true
                    }

                    else -> false
                }
            }
        }

        val archiveMenuItem = bab.menu.findItem(R.id.archive_note)

        etNotoTitle.doAfterTextChanged {
            viewModel.setNoteTitle(it.toString())
        }
        etNotoBody.doAfterTextChanged {
            viewModel.setNoteBody(it.toString())
        }

        rbNotoStar.setOnClickListener { viewModel.toggleNotoStar() }

        viewModel.note
            .onEach {
                etNotoTitle.setText(it.title)
                etNotoBody.setText(it.body)
                etNotoTitle.setSelection(it.title.length)
                etNotoBody.setSelection(it.body.length)
                rbNotoStar.isChecked = it.isStarred


                if (it.isArchived) archiveMenuItem.icon = drawableResource(R.drawable.ic_outline_unarchive_24)
                else archiveMenuItem.icon = drawableResource(R.drawable.archive_arrow_down_outline)

                if (it.reminderDate == null) fab.setImageDrawable(drawableResource(R.drawable.bell_plus_outline))
                else fab.setImageDrawable(drawableResource(R.drawable.bell_ring_outline))

                it.creationDate.apply {
                    val dateFormat = if (year > ZonedDateTime.now().year) format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
                    else format(DateTimeFormatter.ofPattern("EEE, MMM d"))

                    tvCreatedAt.text = "${getString(R.string.created_at)} ${dateFormat.toUpperCase()}"
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