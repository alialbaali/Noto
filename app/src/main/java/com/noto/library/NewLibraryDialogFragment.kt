package com.noto.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.res.ResourcesCompat
import com.noto.BaseBottomSheetDialogFragment
import com.noto.R
import com.noto.databinding.FragmentDialogLibraryNewBinding
import com.noto.domain.model.Library
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.util.getValue
import org.koin.android.viewmodel.ext.android.viewModel

class NewLibraryDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogLibraryNewBinding

    private val viewModel by viewModel<LibraryListViewModel>()

    private val library: Library = Library(libraryPosition = 0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogLibraryNewBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@NewLibraryDialogFragment
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
            library.notoColor = notoColor
            binding.til.setEndIconTintList(binding.root.resources.getColorStateList(notoColor.getValue()))
        }

        binding.rgNotoIcons.setOnCheckedChangeListener { _, checkedId ->
            val notoIcon = NotoIcon.values()[checkedId]
            library.notoIcon = notoIcon
            binding.til.startIconDrawable = ResourcesCompat.getDrawable(resources, notoIcon.getValue(), null)
        }

        binding.btnCreate.setOnClickListener {

            val title = binding.et.text.toString()

            when {
                title.isBlank() -> binding.til.error = "Title can't be empty"

                viewModel.libraries.value?.any { it.libraryTitle == title } == true -> binding.til.error = "Title already exists"

                else -> {
                    library.libraryTitle = title
                    val new = library.copy(libraryPosition = 0)
                    viewModel.saveLibrary(new)
                    dismiss()
                }
            }
        }

        return binding.root
    }

}