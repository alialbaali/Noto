package com.noto.label

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.noto.R
import com.noto.databinding.DialogChooserLabelBinding
import com.noto.domain.model.Label
import com.noto.noto.NotoViewModel
import com.noto.util.getValue
import timber.log.Timber

class LabelSelectorDialog(context: Context, private val viewModel: NotoViewModel) : BottomSheetDialog(context, R.style.BottomSheetDialog) {

    private val binding = DialogChooserLabelBinding.inflate(layoutInflater)

    init {
        create()
        show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val list = mutableListOf<Label>()

        val labels = viewModel.labels.value!!

        val notoLabels = viewModel.notoLabels.value!!
        Timber.i(notoLabels.toString())

        if (labels.isEmpty()) {
            binding.svCg.visibility = View.GONE
            binding.cg.visibility = View.GONE
            binding.tvLabel.visibility = View.GONE
            binding.btnDone.visibility = View.GONE
            binding.tvPlaceHolder.visibility = View.VISIBLE
            binding.ivPlaceHolder.visibility = View.VISIBLE
        } else {

            for (label in labels) {

                val chip = Chip(context).also { chip ->

                    chip.isChecked = notoLabels.any { it.labelId == label.labelId }

                    chip.id = label.labelId.toInt()
                    chip.chipBackgroundColor = ColorStateList.valueOf(context.resources.getColor(label.notoColor.getValue()))
                    chip.text = label.labelTitle
                    chip.setTextColor(context.resources.getColor(R.color.colorPrimary))
                    chip.gravity = Gravity.CENTER
                    chip.isCheckable = true

                    chip.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            list.add(label)
                        } else {
                            list.remove(label)
                        }
                    }
                }
                binding.cg.addView(chip)
            }
            binding.btnDone.setOnClickListener {
                viewModel._notoLabels.postValue(list)
                dismiss()
            }
        }
    }

}