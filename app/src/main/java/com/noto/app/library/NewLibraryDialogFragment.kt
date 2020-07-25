package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FragmentDialogLibraryNewBinding
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.app.library.NewLibraryDialogFragmentArgs
import com.noto.app.util.getValue
import org.koin.android.viewmodel.ext.android.sharedViewModel

class NewLibraryDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogLibraryNewBinding

    private val viewModel by sharedViewModel<LibraryViewModel>()

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogLibraryNewBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@NewLibraryDialogFragment
            viewModel = this@NewLibraryDialogFragment.viewModel
        }

        if (args.libraryId == 0L) viewModel.postLibrary()
        else binding.btnCreate.text = getString(R.string.update_library)

        viewModel.library.observe(viewLifecycleOwner) {library ->
            binding.til.setEndIconTintList(ResourcesCompat.getColorStateList(resources, library.notoColor.getValue(), null))
            binding.til.startIconDrawable = ResourcesCompat.getDrawable(resources, library.notoIcon.getValue(), null)
        }

        for (notoColor in NotoColor.values()) {
            val radBtn = RadioButton(context)
            radBtn.id = notoColor.ordinal
            radBtn.buttonDrawable = ResourcesCompat.getDrawable(resources, R.drawable.selector_dialog_rbtn_gray, null)
            radBtn.buttonTintList = ResourcesCompat.getColorStateList(resources, notoColor.getValue(), null)
            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(24, 16, 24, 16)
            radBtn.layoutParams = layoutParams
            binding.rgNotoColors.addView(radBtn)
        }

        for (notoIcon in NotoIcon.values()) {
            val radBtn = RadioButton(context)
            radBtn.id = notoIcon.ordinal
            radBtn.buttonDrawable = ResourcesCompat.getDrawable(resources, notoIcon.getValue(), null)
            radBtn.buttonTintList = ResourcesCompat.getColorStateList(resources, R.color.colorOnSecondary, null)
            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(24, 16, 24, 16)
            radBtn.layoutParams = layoutParams
            binding.rgNotoIcons.addView(radBtn)
        }

        binding.rgNotoColors.setOnCheckedChangeListener { _, checkedId ->
            val notoColor = NotoColor.values()[checkedId]
            viewModel.setNotoColor(notoColor)
            binding.til.setEndIconTintList(ResourcesCompat.getColorStateList(resources, notoColor.getValue(), null))
        }

        binding.rgNotoIcons.setOnCheckedChangeListener { _, checkedId ->
            val notoIcon = NotoIcon.values()[checkedId]
            viewModel.setNotoIcon(notoIcon)
            binding.til.startIconDrawable = ResourcesCompat.getDrawable(resources, notoIcon.getValue(), null)
        }

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

}