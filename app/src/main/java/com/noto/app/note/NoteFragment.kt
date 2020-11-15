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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.util.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val NOTO_ID = "noto_id"
const val NOTO_TITLE = "noto_title"
const val NOTO_BODY = "noto_body"
const val NOTO_COLOR = "noto_color"
const val NOTO_ICON = "noto_icon"

class NoteFragment : Fragment() {

    private lateinit var binding: NoteFragmentBinding

    private val viewModel by sharedViewModel<NoteViewModel>()

    private val args by navArgs<NoteFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = NoteFragmentBinding.inflate(inflater, container, false).apply {
            fab.setOnClickListener {
                findNavController().navigate(NoteFragmentDirections.actionNotoFragmentToReminderDialogFragment())
            }
        }

        viewModel.getLibraryById(args.libraryId)

        if (args.noteId == 0L) {

            viewModel.postNote(args.libraryId)

            binding.etNotoBody.requestFocus()
            imm.showSoftInput(binding.etNotoBody, InputMethodManager.SHOW_IMPLICIT)

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                this@NoteFragment.findNavController().navigateUp()
                viewModel.createNote()
            }.isEnabled = true

            binding.tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.etNotoBody.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                findNavController().navigateUp()
                viewModel.createNote()
            }

        } else {

            viewModel.getNoteById(args.noteId)

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                this@NoteFragment.findNavController().navigateUp()
                viewModel.updateNote()
            }.isEnabled = true


            binding.tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.etNotoBody.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                findNavController().navigateUp()
                viewModel.updateNote()
            }

        }

        binding.nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

        with(binding.bab) {

            navigationIcon?.mutate()?.setTint(colorResource(R.color.colorPrimary))

            setNavigationOnClickListener {
                findNavController().navigate(NoteFragmentDirections.actionNotoFragmentToNotoDialogFragment(args.libraryId, args.noteId))
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {

                    R.id.share_noto -> {

                        val noto = viewModel.note.value

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, noto?.title)
                            putExtra(Intent.EXTRA_TEXT, noto?.body)
                        }

                        val chooser = Intent.createChooser(intent, "${getString(R.string.share)} ${noto?.title} to")

                        startActivity(chooser)

                        true
                    }

                    R.id.archive_noto -> {

                        menuItem.icon = drawableResource(R.drawable.ic_outline_unarchive_24)

                        if (viewModel.note.value?.isArchived == true) {
                            viewModel.setNotoArchived(false)
                            binding.root.snackbar(getString(R.string.noto_unarchived))
                        } else {
                            viewModel.setNotoArchived(true)
                            binding.root.snackbar(getString(R.string.noto_archived))
                        }

                        true
                    }

                    else -> false
                }
            }
        }

        val archiveMenuItem = binding.bab.menu.findItem(R.id.archive_noto)

        binding.etNotoTitle.doAfterTextChanged {
            viewModel.setNotoTitle(it.toString())
        }
        binding.etNotoBody.doAfterTextChanged {
            viewModel.setNotoBody(it.toString())
        }

        binding.rbNotoStar.setOnClickListener { viewModel.toggleNotoStar() }

        viewModel.note.observe(viewLifecycleOwner) {
            it?.let { noto ->

                binding.etNotoTitle.setText(it.title)
                binding.etNotoBody.setText(it.body)
                binding.etNotoTitle.setSelection(it.title.length)
                binding.etNotoBody.setSelection(it.body.length)
                binding.rbNotoStar.isChecked = it.isStarred


                if (noto.isArchived) archiveMenuItem.icon = drawableResource(R.drawable.ic_outline_unarchive_24)
                else archiveMenuItem.icon = drawableResource(R.drawable.archive_arrow_down_outline)

                if (noto.reminderDate == null) binding.fab.setImageDrawable(drawableResource(R.drawable.bell_plus_outline))
                else binding.fab.setImageDrawable(drawableResource(R.drawable.bell_ring_outline))

                noto.creationDate.apply {
                    val dateFormat = if (year > ZonedDateTime.now().year) format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
                    else format(DateTimeFormatter.ofPattern("EEE, MMM d"))

                    binding.tvCreatedAt.text = "${getString(R.string.created_at)} ${dateFormat.toUpperCase(Locale.getDefault())}"
                }
            }
        }

        viewModel.library.observe(viewLifecycleOwner) { library ->
            val color = colorResource(library.color.toResource())

            binding.tvLibraryTitle.text = library.title
            binding.tvLibraryTitle.setTextColor(color)
            binding.tvCreatedAt.setTextColor(color)
            binding.tb.navigationIcon?.mutate()?.setTint(color)
            binding.fab.backgroundTintList = colorStateResource(library.color.toResource())
        }

        return binding.root
    }


}