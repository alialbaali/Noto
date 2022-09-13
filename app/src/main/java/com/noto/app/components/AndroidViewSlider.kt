package com.noto.app.components

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.noto.app.databinding.SliderViewBinding

@Composable
fun AndroidViewSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelFormatter: (Float) -> String = {
        when (it) {
            0F -> it.toInt().toString()
            1F -> it.toInt().toString()
            else -> it.toString().take(4)
        }
    },
    valueRange: ClosedFloatingPointRange<Float> = 0F..1F,
    stepSize: Float = 0F,
    trackInActiveColorStateList: ColorStateList? = null,
) = AndroidViewBinding(
    factory = { inflater, parent, attachToParent ->
        SliderViewBinding.inflate(inflater, parent, attachToParent).apply {
            slider.addOnChangeListener { _, level, _ -> onValueChange(level) }
            slider.setLabelFormatter(labelFormatter)
            slider.valueFrom = valueRange.start
            slider.valueTo = valueRange.endInclusive
            slider.stepSize = stepSize
            slider.contentDescription = contentDescription
            slider.value = value
            if (trackInActiveColorStateList != null) {
                slider.trackInactiveTintList = trackInActiveColorStateList
            }
        }
    },
    modifier = modifier.heightIn(max = 24.dp),
    update = {
        val parent = root.parent as? ViewGroup
        parent?.apply { clipChildren = false }
        slider.isEnabled = enabled
    }
)