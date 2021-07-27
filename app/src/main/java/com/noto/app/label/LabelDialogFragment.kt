package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.LabelDialogFragmentBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.toResource
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LabelDialogFragment : BaseDialogFragment() {

    private lateinit var binding: LabelDialogFragmentBinding

    private val viewModel by sharedViewModel<LabelViewModel>()

    private val args by navArgs<LabelDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = LabelDialogFragmentBinding.inflate(inflater, container, false)

        var labelColor = NotoColor.GRAY

        if (args.labelId == 0L) {

            binding.btnCreate.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE

            binding.btnCreate.setOnClickListener {
                val labelTitle = binding.et.text.toString()

                if (labelTitle.isBlank()) {
                    binding.til.error = "Title can't be empty!"
                } else {

                    dismiss()

                    val label = Label(labelTitle = labelTitle, labelColor = labelColor)

                    viewModel.saveLabel(label)

                }


            }

        } else {

            binding.btnCreate.visibility = View.GONE
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.VISIBLE

            viewModel.getLabelById(args.labelId)

            viewModel.label.observe(viewLifecycleOwner) { label ->

                binding.et.setText(label.labelTitle)

                labelColor = label.labelColor

                binding.til.setStartIconTintList(ResourcesCompat.getColorStateList(resources, label.labelColor.toResource(), null))

            }

            binding.btnUpdate.setOnClickListener {

                val labelTitle = binding.et.text.toString()

                if (labelTitle.isBlank()) {
                    binding.til.error = "Title can't be empty!"
                } else {

                    dismiss()

                    val label = viewModel.label.value!!.copy(labelTitle = labelTitle, labelColor = labelColor)

                    viewModel.saveLabel(label)

                }

            }

            binding.btnDelete.setOnClickListener {

                dismiss()

                viewModel.deleteLabel(viewModel.label.value!!)

            }

        }

        NotoColor.values().forEach { notoColor ->

            val radBtn = RadioButton(context)

            radBtn.id = notoColor.ordinal

            radBtn.buttonDrawable = ResourcesCompat.getDrawable(resources, R.drawable.selector_dialog_rbtn_gray, null)

            radBtn.buttonTintList = ResourcesCompat.getColorStateList(resources, notoColor.toResource(), null)

            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(24, 16, 24, 16)

            radBtn.layoutParams = layoutParams

            binding.rgNotoColors.addView(radBtn)

        }

        binding.rgNotoColors.setOnCheckedChangeListener { group, checkedId ->
            labelColor = NotoColor.values()[checkedId]
            binding.til.setStartIconTintList(ResourcesCompat.getColorStateList(resources, labelColor.toResource(), null))
        }

        return binding.root
    }


}