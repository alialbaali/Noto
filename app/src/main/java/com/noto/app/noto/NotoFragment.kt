package com.noto.app.noto

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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.FragmentNotoBinding
import com.noto.app.noto.NotoFragmentArgs
import com.noto.app.util.getValue
import com.noto.app.util.snackbar
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val NOTO_ID = "noto_id"
const val NOTO_TITLE = "noto_title"
const val NOTO_BODY = "noto_body"
const val NOTO_COLOR = "noto_color"
const val NOTO_ICON = "noto_icon"

class NotoFragment : Fragment() {

    private lateinit var binding: FragmentNotoBinding

    private val viewModel by sharedViewModel<NotoViewModel>()

    private val args by navArgs<NotoFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotoBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@NotoFragment
            viewModel = this@NotoFragment.viewModel
        }

        viewModel.getLibraryById(args.libraryId)

        if (args.notoId == 0L) {

            viewModel.postNoto(args.libraryId)

            binding.etNotoBody.requestFocus()
            imm.showSoftInput(binding.etNotoBody, InputMethodManager.SHOW_IMPLICIT)

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                this@NotoFragment.findNavController().navigateUp()
                viewModel.createNoto()
//                viewModel.createNotoWithLabels()
            }.isEnabled = true

            binding.tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.etNotoBody.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                findNavController().navigateUp()
                viewModel.createNoto()
//                viewModel.createNotoWithLabels()
            }

        } else {

            viewModel.getNotoById(args.notoId)

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                this@NotoFragment.findNavController().navigateUp()
                viewModel.updateNoto()
            }.isEnabled = true


            binding.tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.etNotoBody.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                findNavController().navigateUp()
                viewModel.updateNoto()
            }

        }

        binding.nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
//        binding.chip.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
//        binding.chip.setOnClickListener { findNavController().navigate(NotoFragmentDirections.actionNotoFragmentToLabelChooserDialogFragment()) }

        with(binding.fab) {

            imageTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBackground, null)

            setOnClickListener {
                findNavController().navigate(NotoFragmentDirections.actionNotoFragmentToReminderDialogFragment())
            }

        }

        with(binding.bab) {

            navigationIcon?.mutate()?.setTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))

            setNavigationOnClickListener {
                findNavController().navigate(NotoFragmentDirections.actionNotoFragmentToNotoDialogFragment(args.libraryId, args.notoId))
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {

                    R.id.share_noto -> {

                        val noto = viewModel.noto.value

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, noto?.notoTitle)
                            putExtra(Intent.EXTRA_TEXT, noto?.notoBody)
                        }

                        val chooser = Intent.createChooser(intent, "${getString(R.string.share)} ${noto?.notoTitle} to")

                        startActivity(chooser)

                        true
                    }

                    R.id.archive_noto -> {

                        menuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_outline_unarchive_24, null)

                        if (viewModel.noto.value?.notoIsArchived == true) {
                            viewModel.setArchived(false)
                            binding.root.snackbar(getString(R.string.noto_unarchived))
                        } else {
                            viewModel.setArchived(true)
                            binding.root.snackbar(getString(R.string.noto_archived))
                        }

                        true
                    }

                    else -> false
                }
            }
        }

        val archiveMenuItem = binding.bab.menu.findItem(R.id.archive_noto)


        viewModel.noto.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            it?.let { noto ->


                if (noto.notoIsArchived) archiveMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_outline_unarchive_24, null)
                else archiveMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.archive_arrow_down_outline, null)

                if (noto.notoReminder == null) binding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bell_plus_outline, null))
                else binding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bell_ring_outline, null))

                noto.notoCreationDate.apply {
                    val dateFormat = if (year > ZonedDateTime.now().year) format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
                    else format(DateTimeFormatter.ofPattern("EEE, MMM d"))

                    binding.tvCreatedAt.text = "${getString(R.string.created_at)} ${dateFormat.toUpperCase(Locale.getDefault())}"
                }
            }
        })

        viewModel.library.observe(viewLifecycleOwner) { library ->
            val color = ResourcesCompat.getColor(resources, library.notoColor.getValue(), null)

            binding.tvLibraryTitle.text = library.libraryTitle
            binding.tvLibraryTitle.setTextColor(color)
            binding.tvCreatedAt.setTextColor(color)
            binding.tb.navigationIcon?.mutate()?.setTint(color)
            binding.fab.backgroundTintList = ResourcesCompat.getColorStateList(resources, library.notoColor.getValue(), null)
//            binding.chip.setTextColor(color)
//            binding.chip.chipIconTint = ColorStateList.valueOf(color)
//            binding.chip.chipStrokeColor = ColorStateList.valueOf(color)

        }


//        viewModel.labels.observe(viewLifecycleOwner) { labels ->
//
//
//            labels.forEach { label ->
//
//                val labelColor = label.labelColor.getValue()
//
//                val textColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
//
//                val chip = Chip(requireContext()).apply {
//
//                    id = label.labelId.toInt()
//                    setChipBackgroundColorResource(labelColor)
//                    setTextColor(textColor)
//                    text = label.labelTitle
//                    chipEndPadding = 8.dp(requireContext())
//                    chipStartPadding = 8.dp(requireContext())
//                    chipMinHeight = 42.dp(requireContext())
//                    typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
//
//                }
//
//                binding.cg.findViewById<Chip>(chip.id) ?: binding.cg.addView(chip)
//
//            }
//
//        }

        return binding.root
    }


}