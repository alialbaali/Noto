package com.noto.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.databinding.FragmentArchiveBinding
import com.noto.domain.model.Noto
import com.noto.util.getValue
import org.koin.android.viewmodel.ext.android.viewModel

class ArchiveFragment : Fragment() {

    private lateinit var binding: FragmentArchiveBinding

    private val viewModel by viewModel<LibraryViewModel>()

    private val args by navArgs<ArchiveFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentArchiveBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ArchiveFragment
        }

        viewModel.getArchivedNotos(args.libraryId)
        viewModel.getLibraryById(args.libraryId)

        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        with(binding.rv) {

            val rvAdapter = NotoListRVAdapter(viewModel, object : NotoItemClickListener {

                override fun onClick(noto: Noto) {
                    findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToArchiveDialogFragment(noto.notoId))
                }

                override fun onLongClick(noto: Noto) {
                    findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToArchiveDialogFragment(noto.notoId))
                }

            })

            binding.rv.adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            viewModel.notos.observe(viewLifecycleOwner) { notos ->
                binding.tvLibraryNotoCount.text = notos.size.toString().plus(if (notos.size == 1) " Archived Noto" else " Archived Notos")
                rvAdapter.submitList(notos)
            }

        }

        viewModel.library.observe(viewLifecycleOwner) { library ->
            if (args.libraryId != 0L) {
                val color = ResourcesCompat.getColor(resources, library.notoColor.getValue(), null)
                binding.tb.navigationIcon?.mutate()?.setTint(color)
                binding.ivLibraryIcon.imageTintList = ResourcesCompat.getColorStateList(resources, library.notoColor.getValue(), null)
                binding.tvLibraryNotoCount.setTextColor(color)
                binding.tvLibraryTitle.setTextColor(color)
                binding.tvLibraryTitle.text = "${library.libraryTitle} ${getString(R.string.archived_notos)}"
            }
        }

        return binding.root
    }

}