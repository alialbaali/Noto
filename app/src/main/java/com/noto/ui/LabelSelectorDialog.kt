package com.noto.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.noto.R
import com.noto.databinding.DialogSelectorLabelBinding
import com.noto.domain.Label
import com.noto.viewModel.NotoViewModel
import timber.log.Timber

class LabelSelectorDialog(context: Context, private val viewModel: NotoViewModel) : BottomSheetDialog(context, R.style.BottomSheetDialog) {

    private val binding = DialogSelectorLabelBinding.inflate(layoutInflater)

    init {
        create()
        show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val labels = viewModel.labels.value!!

        Timber.i("LABELS $labels")

        val notoLabels = viewModel.notoLabels.value!!

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

                    chip.isChecked = notoLabels.contains(label)

                    chip.id = label.labelId.toInt()
                    chip.chipBackgroundColor = ColorStateList.valueOf(context.getColor(label.notoColor.resId))
                    chip.text = label.labelTitle
                    chip.setTextColor(context.getColor(R.color.colorPrimary))
                    chip.gravity = Gravity.CENTER
                    chip.isCheckable = true
                    chip.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            viewModel.notoLabels.value!!.add(label)
                        } else {
                            viewModel.notoLabels.value!!.remove(label)
                        }
                    }
                }
                binding.cg.addView(chip)
            }
            binding.btnDone.setOnClickListener {
                dismiss()
            }
        }
    }

}