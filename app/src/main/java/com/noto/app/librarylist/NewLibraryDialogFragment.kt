package com.noto.app.librarylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLibraryDialogFragmentBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.NotoIcon
import com.noto.app.notelist.NoteListViewModel
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewLibraryDialogFragment : BaseDialogFragment() {

    private lateinit var binding: NewLibraryDialogFragmentBinding

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = NewLibraryDialogFragmentBinding.inflate(inflater, container, false)

        BaseDialogFragmentBinding.bind(binding.root).apply {
            if (args.libraryId == 0L) {
                tvDialogTitle.text = stringResource(R.string.new_library)
            } else {
                tvDialogTitle.text = stringResource(R.string.edit_library)
                binding.btnCreate.text = getString(R.string.update_library)
            }
        }

        setupNotoColors()
        setupNotoIcons()
        collectState()
        setupListeners()

        return binding.root
    }

    private fun collectState() {
        viewModel.library
            .onEach { binding.et.setText(it.title) }
            .launchIn(lifecycleScope)
    }

    private fun setupListeners() {
        binding.btnCreate.setOnClickListener {
            val title = binding.et.text.toString()
            val color = binding.rgNotoColors.checkedRadioButtonId.let {
                NotoColor.values().getOrElse(it) {
                    viewModel.library.value.color
                }
            }
            val icon = binding.rgNotoIcons.checkedRadioButtonId.let {
                NotoIcon.values().getOrElse(it) {
                    viewModel.library.value.icon
                }
            }

            if (title.isBlank()) {
                binding.til.error = stringResource(R.string.empty_title)
            } else {
                dismiss()
                viewModel.createOrUpdateLibrary(title, color, icon)
            }
        }

    }

    private fun setupNotoColors() {
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
                binding.til.setEndIconTintList(colorStateResource(toResource()))
            }
        }
    }

    private fun setupNotoIcons() {
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
                binding.til.startIconDrawable = drawableResource(toResource())
            }
        }
    }
}