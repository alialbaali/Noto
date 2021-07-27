package com.noto.app.label

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.ScaleAnimation
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.LabelChooserDialogFragmentBinding
import com.noto.app.note.NoteViewModel
import com.noto.app.util.dp
import com.noto.app.util.toResource
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LabelChooserDialogFragment : BaseDialogFragment() {

    private lateinit var binding: LabelChooserDialogFragmentBinding

    private val notoViewModel by sharedViewModel<NoteViewModel>()

    private val labelViewModel by sharedViewModel<LabelViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = LabelChooserDialogFragmentBinding.inflate(inflater, container, false)

        val textColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

        labelViewModel.labels.observe(viewLifecycleOwner) { labels ->

            binding.cg.removeAllViews()

            labels.forEach { label ->

                val labelColor = label.labelColor.toResource()

                val resourceLabelColor = ResourcesCompat.getColor(resources, labelColor, null)

                val chip = Chip(requireContext()).apply {

                    setChipBackgroundColorResource(R.color.colorBackground)
                    setTextColor(resourceLabelColor)
                    chipStrokeColor = ColorStateList.valueOf(resourceLabelColor)
                    chipStrokeWidth = 5F
                    textSize = 16F
                    text = label.labelTitle
                    isCheckable = true
                    isChecked = notoViewModel.labels.value?.contains(label) ?: false
                    isCheckedIconVisible = false
                    chipEndPadding = 8.dp(requireContext())
                    chipStartPadding = 8.dp(requireContext())
                    chipMinHeight = 42.dp(requireContext())
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.arima_madurai_medium)

                    setOnCheckedChangeListener { _, isChecked ->

                        if (isChecked) {
                            setChipBackgroundColorResource(labelColor)
                            setTextColor(textColor)
                            chipStrokeColor = null
                            notoViewModel.labels.value?.add(label)
                        } else {
                            setChipBackgroundColorResource(R.color.colorBackground)
                            setTextColor(resourceLabelColor)
                            chipStrokeColor = ColorStateList.valueOf(resourceLabelColor)
                            notoViewModel.labels.value?.remove(label)
                            notoViewModel.labels.value.also { println(it.toString()) }
                        }

                        notoViewModel.notifyLabelsObserver()

                        val animation = AlphaAnimation(0.25f, 1f).apply { duration = 250 }

                        startAnimation(animation)

                    }

                }

                binding.cg.addView(chip)
            }
        }


        val scaleAnimation = ScaleAnimation(0.95f, 1.0f, 0.95f, 1.0f).apply {
            duration = 250
        }

        return binding.root
    }
//
//        val list = mutableListOf<Label>()
//
////        val labels = viewModel.labels
//
//        val notoLabels = viewModel.notoLabels.value!!
//        Timber.i(notoLabels.toString())
//
//        if (labels.isEmpty()) {
//            binding.svCg.visibility = View.GONE
//            binding.cg.visibility = View.GONE
//            binding.tvLabel.visibility = View.GONE
//            binding.btnDone.visibility = View.GONE
//            binding.tvPlaceHolder.visibility = View.VISIBLE
//            binding.ivPlaceHolder.visibility = View.VISIBLE
//        } else {
//
//            for (label in labels) {
//
//                val chip = Chip(context).also { chip ->
//
//                    chip.isChecked = notoLabels.any { it.labelId == label.labelId }
//
//                    chip.id = label.labelId.toInt()
////                    chip.chipBackgroundColor = ColorStateList.valueOf(context.resources.getColor(label.notoColor.getValue()))
//                    chip.text = label.labelTitle
//                    chip.setTextColor(context.resources.getColor(R.color.colorPrimary))
//                    chip.gravity = Gravity.CENTER
//                    chip.isCheckable = true
//
//                    chip.setOnCheckedChangeListener { _, isChecked ->
//                        if (isChecked) {
//                            list.add(label)
//                        } else {
//                            list.remove(label)
//                        }
//                    }
//                }
//                binding.cg.addView(chip)
//            }
//            binding.btnDone.setOnClickListener {
//                viewModel._notoLabels.postValue(list)
//                dismiss()
//            }
//        }

}