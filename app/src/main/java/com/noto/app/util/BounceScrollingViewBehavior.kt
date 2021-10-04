package com.noto.app.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.appbar.AppBarLayout

private const val OverScrollArea = 8

class BounceScrollingViewBehavior(context: Context?, attrs: AttributeSet?) : AppBarLayout.ScrollingViewBehavior(context, attrs) {

    private var overScrollY = 0

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        overScrollY = 0
        return true
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyUnconsumed == 0) return
        overScrollY -= if (type == ViewCompat.TYPE_TOUCH) dyUnconsumed / OverScrollArea else dyUnconsumed
        (target as ViewGroup).forEach { view ->
            view.translationY = overScrollY.toFloat()
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) = animationToZeroPosition(target)

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // Scroll view by inertia when current position equals to 0
        if (overScrollY == 0) return false
        // Smooth animate to 0 when user fling view
        animationToZeroPosition(target)
        return true
    }

    private fun animationToZeroPosition(target: View) {
        (target as ViewGroup).forEach { view ->
            SpringAnimation(view, SpringAnimation.TRANSLATION_Y)
                .setSpring(
                    SpringForce()
                        .setFinalPosition(0f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                        .setStiffness(SpringForce.STIFFNESS_LOW)
                )
                .start()
        }
    }
}
