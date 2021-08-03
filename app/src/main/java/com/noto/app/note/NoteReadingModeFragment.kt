package com.noto.app.note

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteReadingModeFragmentBinding
import com.noto.app.util.colorResource
import com.noto.app.util.formatCreationDate
import com.noto.app.util.stringResource
import com.noto.app.util.toResource
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteReadingModeFragment : Fragment() {

    private lateinit var binding: NoteReadingModeFragmentBinding

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteReadingModeFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = NoteReadingModeFragmentBinding.inflate(inflater, container, false)

        binding.nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.note
            .filterNotNull()
            .onEach {
                binding.etNoteTitle.text = it.title
                binding.etNoteBody.text = it.body
                binding.tvCreatedAt.text = "${resources.stringResource(R.string.created_at)} ${it.formatCreationDate()}"
            }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach {
                val color = resources.colorResource(it.color.toResource())

                binding.tb.title = it.title
                binding.tb.setTitleTextColor(color)
                binding.tvCreatedAt.setTextColor(color)
                binding.tb.navigationIcon?.mutate()?.setTint(color)
            }
            .launchIn(lifecycleScope)
        return binding.root
    }

}