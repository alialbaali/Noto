package com.noto.app.util

import android.animation.ArgbEvaluator
import android.animation.IntEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.noto.app.R
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

const val DefaultAnimationDuration = 250L
typealias DefaultInterpolator = AccelerateInterpolator

@Suppress("FunctionName")
fun VerticalListItemAnimator() = SlideInUpAnimator(DefaultInterpolator()).apply {
    addDuration = DefaultAnimationDuration
    changeDuration = DefaultAnimationDuration
    moveDuration = DefaultAnimationDuration
    removeDuration = DefaultAnimationDuration
}

@Suppress("FunctionName")
fun HorizontalListItemAnimator() = ScaleInAnimator(DefaultInterpolator()).apply {
    addDuration = DefaultAnimationDuration
    changeDuration = DefaultAnimationDuration
    moveDuration = DefaultAnimationDuration
    removeDuration = DefaultAnimationDuration
}

fun View.animateBackgroundColor(fromColor: Int, toColor: Int): ValueAnimator? {
    return ValueAnimator.ofArgb(fromColor, toColor)
        .apply {
            duration = DefaultAnimationDuration
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                background?.mutate()
                    ?.also { it.setRippleColor(fromColor.toColorStateList()) }
                    ?.setTint(value)
            }
            start()
        }
}

fun View.animateLabelColors(fromColor: Int, toColor: Int) {
    ValueAnimator.ofPropertyValuesHolder(
        PropertyValuesHolder.ofObject("background", ArgbEvaluator(), fromColor, toColor),
        PropertyValuesHolder.ofObject("stroke", IntEvaluator(), 0.dp, LabelDefaultStrokeWidth),
    ).apply {
        duration = DefaultAnimationDuration
        addUpdateListener { animator ->
            val backgroundColor = animator.getAnimatedValue("background") as Int
            val strokeWidth = animator.getAnimatedValue("stroke") as Int
            background = context.drawableResource(R.drawable.label_item_shape)
                ?.mutate()
                ?.let { it as RippleDrawable }
                ?.let { it.getDrawable(0) as GradientDrawable }
                ?.apply {
                    setStroke(strokeWidth, fromColor)
                    cornerRadius = LabelDefaultCornerRadius
                    setColor(backgroundColor)
                }
                ?.toRippleDrawable(context)
                ?.also { it.setRippleColor(fromColor.toColorStateList()) }
        }
        start()
    }
}

fun TextView.animateTextColor(fromColor: Int, toColor: Int): ValueAnimator? {
    return ValueAnimator.ofArgb(fromColor, toColor)
        .apply {
            duration = DefaultAnimationDuration
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                setTextColor(value)
            }
            start()
        }
}