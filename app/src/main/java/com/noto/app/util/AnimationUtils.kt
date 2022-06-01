package com.noto.app.util

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis
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

private fun Visibility.applyDefaultConfig() = apply {
    duration = DefaultAnimationDuration
    interpolator = DefaultInterpolator()
}

fun Fragment.setupMixedTransitions() {
    exitTransition = MaterialElevationScale(false).applyDefaultConfig()
    reenterTransition = MaterialElevationScale(true).applyDefaultConfig()
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).applyDefaultConfig()
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).applyDefaultConfig()
}

fun Fragment.setupFadeTransition() {
    exitTransition = MaterialFade().applyDefaultConfig()
    enterTransition = MaterialFade().applyDefaultConfig()
    reenterTransition = MaterialFade().applyDefaultConfig()
    returnTransition = MaterialFade().applyDefaultConfig()
}