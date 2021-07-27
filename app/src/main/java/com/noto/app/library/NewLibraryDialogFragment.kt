package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLibraryDialogFragmentBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.NotoIcon
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewLibraryDialogFragment : BaseDialogFragment() {

    private lateinit var binding: NewLibraryDialogFragmentBinding

    private val viewModel by sharedViewModel<LibraryViewModel>()

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = NewLibraryDialogFragmentBinding.inflate(inflater, container, false)

        binding.apply {
            val baseDialog = BaseDialogFragmentBinding.bind(root)

            baseDialog.tvDialogTitle.text = stringResource(R.string.new_library)

            initNotoColors()
            initNotoIcons()

            if (args.libraryId == 0L) viewModel.postLibrary()
            else btnCreate.text = getString(R.string.update_library)

            viewModel.library.observe(viewLifecycleOwner) { library ->
                library?.let {
                    et.setText(it.title)
                    et.setSelection(it.title.length)
                    til.setEndIconTintList(colorStateResource(it.color.toResource()))
                    til.startIconDrawable = drawableResource(it.icon.toResource())
                }
            }

            et.doAfterTextChanged { viewModel.setLibraryTitle(it.toString()) }

            btnCreate.setOnClickListener {

                val title = et.text.toString()

                if (title.isBlank()) til.error = stringResource(R.string.empty_title)
                else {
                    dismiss()
                    if (args.libraryId == 0L) viewModel.createLibrary()
                    else viewModel.updateLibrary()
                }
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
                scaleX = 1.25F
                scaleY = 1.25F
                val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(48, 48, 48, 48)
                this.layoutParams = layoutParams
                binding.rgNotoColors.addView(this)
            }
        }
        binding.rgNotoColors.setOnCheckedChangeListener { _, checkedId ->
            NotoColor.values()[checkedId].apply {
                binding.rgNotoColors.children
                    .map { it as RadioButton }
                    .onEach {
                        if (it.id == checkedId) {
                            it.buttonDrawable = drawableResource(R.drawable.ic_sort_checked)
                            it.buttonTintList = colorStateResource(toResource())
                        } else {
                            it.buttonDrawable = drawableResource(R.drawable.selector_dialog_rbtn_gray)
                            it.buttonTintList = colorStateResource(toResource())
                        }
                    }
                viewModel.setNotoColor(this)
            }
        }
    }

    private fun initNotoIcons() {
        NotoIcon.values().forEach { notoIcon ->
            RadioButton(context).apply {
                id = notoIcon.ordinal
                buttonDrawable = drawableResource(notoIcon.toResource())
                buttonTintList = colorStateResource(R.color.colorOnSecondary)
                scaleX = 1.25F
                scaleY = 1.25F
                val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(48, 48, 48, 48)
                this.layoutParams = layoutParams
                binding.rgNotoIcons.addView(this)
            }
        }
        binding.rgNotoIcons.setOnCheckedChangeListener { _, checkedId ->
            NotoIcon.values()[checkedId].apply {
                binding.rgNotoIcons.children
                    .map { it as RadioButton }
                    .onEach {
                        if (it.id == checkedId) {
                            it.setBackgroundColor(colorResource(R.color.colorPrimary))
                        } else {
                            it.setBackgroundColor(colorResource(R.color.colorOnSecondary))
                        }
                    }
                viewModel.setNotoIcon(this)
            }
        }
    }
}