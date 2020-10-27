package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FragmentDialogLibraryNewBinding
import com.noto.app.util.colorStateResource
import com.noto.app.util.drawableResource
import com.noto.app.util.toResource
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import org.koin.android.viewmodel.ext.android.sharedViewModel

class NewLibraryDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogLibraryNewBinding

    private val viewModel by sharedViewModel<LibraryViewModel>()

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogLibraryNewBinding.inflate(inflater, container, false)
        initNotoColors()
        initNotoIcons()

        if (args.libraryId == 0L) viewModel.postLibrary()
        else binding.btnCreate.text = getString(R.string.update_library)

        viewModel.library.observe(viewLifecycleOwner) { library ->
            library?.let {
                binding.et.setText(it.libraryTitle)
                binding.et.setSelection(it.libraryTitle.length)
                binding.til.setEndIconTintList(colorStateResource(it.notoColor.toResource()))
                binding.til.startIconDrawable = drawableResource(it.notoIcon.toResource())
            }
        }

        binding.et.doAfterTextChanged { viewModel.setLibraryTitle(it.toString()) }

        binding.btnCreate.setOnClickListener {

            val title = binding.et.text.toString()

            if (title.isBlank()) binding.til.error = "Title can't be empty"
            else {
                dismiss()
                if (args.libraryId == 0L) viewModel.createLibrary()
                else viewModel.updateLibrary()
            }
        }

        return binding.root
    }

    private fun initNotoColors() {
        NotoColor.values().forEach { notoColor ->
            RadioButton(context).apply {
                id = notoColor.ordinal
                buttonDrawable = drawableResource(R.drawable.selector_dialog_rbtn_gray)
                buttonTintList = colorStateResource(notoColor.toResource())
                val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(24, 16, 24, 16)
                this.layoutParams = layoutParams
                binding.rgNotoColors.addView(this)
            }
        }
        binding.rgNotoColors.setOnCheckedChangeListener { _, checkedId ->
            NotoColor.values()[checkedId]
                .apply { viewModel.setNotoColor(this) }
        }
    }

    private fun initNotoIcons() {
        NotoIcon.values().forEach { notoIcon ->
            RadioButton(context).apply {
                id = notoIcon.ordinal
                buttonDrawable = drawableResource(notoIcon.toResource())
                buttonTintList = colorStateResource(R.color.colorOnSecondary)
                val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(24, 16, 24, 16)
                this.layoutParams = layoutParams
                binding.rgNotoIcons.addView(this)
            }
        }
        binding.rgNotoIcons.setOnCheckedChangeListener { _, checkedId ->
            NotoIcon.values()[checkedId]
                .apply { viewModel.setNotoIcon(this) }
        }
    }
}