package com.noto.noto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.R
import com.noto.databinding.FragmentNotoBinding
import com.noto.util.getValue
import com.noto.util.snackbar
import org.koin.android.viewmodel.ext.android.sharedViewModel

const val NOTO_ID = "noto_id"
const val NOTO_TITLE = "noto_title"
const val NOTO_BODY = "noto_body!"

class NotoFragment : Fragment() {

    private lateinit var binding: FragmentNotoBinding

    private val viewModel by sharedViewModel<NotoViewModel>()

    private val args by navArgs<NotoFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

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

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                this@NotoFragment.findNavController().navigateUp()
                viewModel.createNoto()
            }.isEnabled = true

            binding.tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.etNotoBody.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                findNavController().navigateUp()
                viewModel.createNoto()
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


        with(binding.fab) {

            imageTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBackground, null)

            setOnClickListener {
                findNavController().navigate(NotoFragmentDirections.actionNotoFragmentToReminderDialogFragment())
            }

        }

        with(binding.bab) {

            navigationIcon?.mutate()?.setTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))

            setNavigationOnClickListener {

            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_noto -> {
                        findNavController().navigateUp()
                        viewModel.deleteNoto()
                        true
                    }

                    R.id.archive_noto -> {
                        menuItem.actionView?.animate()?.translationY(100F)?.start()
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

        viewModel.noto.observe(viewLifecycleOwner) { noto ->

            if (noto.notoIsArchived) archiveMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_outline_unarchive_24, null)
            else archiveMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.archive_arrow_down_outline, null)

            if (noto.notoReminder == null) binding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bell_plus_outline, null))
            else binding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bell_ring_outline, null))

        }

        viewModel.library.observe(viewLifecycleOwner) { library ->
            val color = ResourcesCompat.getColor(resources, library.notoColor.getValue(), null)

            binding.tb.title = library.libraryTitle
            binding.tb.setTitleTextColor(color)
            binding.tb.navigationIcon?.mutate()?.setTint(color)
            binding.fab.backgroundTintList = ResourcesCompat.getColorStateList(resources, library.notoColor.getValue(), null)
        }

        return binding.root
    }


}