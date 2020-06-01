package com.noto.label

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import com.noto.domain.model.Label
import com.noto.domain.model.NotoColor
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.noto.R
import com.noto.databinding.DialogLabelBinding
import com.noto.util.getValue

class LabelDialog(context: Context, private val viewModel: LabelListViewModel, private val label: Label) :
    BottomSheetDialog(context, R.style.BottomSheetDialog) {

    private val binding = DialogLabelBinding.inflate(layoutInflater)

    init {
        viewModel.updateLabels()
        create()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        show()
        binding.title.requestFocus()
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (label.labelId == 0L) {
            binding.btnCreate.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE
        } else {
            binding.btnCreate.visibility = View.GONE
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.VISIBLE
            binding.title.setText(label.labelTitle)
            binding.til.setStartIconTintList(context.resources.getColorStateList(label.notoColor.getValue(), null))
        }

        binding.btnCreate.setOnClickListener {
            label.labelTitle = binding.title.text.toString()
            if (label.labelTitle.isEmpty()) {
                binding.til.error = "Title can't be empty!"
            } else {
                viewModel.saveLabel(label)
                dismiss()
            }
        }

        binding.btnUpdate.setOnClickListener {
            label.labelTitle = binding.title.text.toString()
            if (label.labelTitle.isEmpty()) {
                binding.til.error = "Title can't be empty!"
            } else {
                viewModel.saveLabel(label)
                dismiss()
            }
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteLabel(label)
            dismiss()
        }


        for (notoColor in NotoColor.values()) {
            val radBtn = RadioButton(context)
            radBtn.id = notoColor.ordinal
            radBtn.buttonDrawable = context.getDrawable(R.drawable.selector_dialog_rbtn_gray)
            radBtn.buttonTintList = context.getColorStateList(notoColor.getValue())
            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(24, 16, 24, 16)
            radBtn.layoutParams = layoutParams
            binding.rgNotoColors.addView(radBtn)
        }

        binding.rgNotoColors.setOnCheckedChangeListener { group, checkedId ->
            val notoColor = NotoColor.values()[checkedId]
            label.notoColor = notoColor
            binding.til.setStartIconTintList(context.getColorStateList(notoColor.getValue()))
        }
    }

}