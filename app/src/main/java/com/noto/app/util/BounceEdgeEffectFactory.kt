package com.noto.app.util

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

// The magnitude of translation distance while the list is over-scrolled.
private const val OverscrollTranslationMagnitude = 0.2F

// The magnitude of translation distance when the list reaches the edge on fling.
private const val FlingTranslationMagnitude = 0.2F

class BounceEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect = object : EdgeEffect(recyclerView.context) {
        val isVertical = direction == DIRECTION_BOTTOM || direction == DIRECTION_TOP
        val sign = if (direction == DIRECTION_BOTTOM || direction == DIRECTION_RIGHT) -1 else 1
        val animation = SpringAnimation(recyclerView, if (isVertical) SpringAnimation.TRANSLATION_Y else SpringAnimation.TRANSLATION_X)
            .setSpring(
                SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW)
            )

        override fun onPull(deltaDistance: Float) {
            super.onPull(deltaDistance)
            handlePull(deltaDistance)
        }

        override fun onPull(deltaDistance: Float, displacement: Float) {
            super.onPull(deltaDistance, displacement)
            handlePull(deltaDistance)
        }

        private fun handlePull(deltaDistance: Float) {
            if (isVertical)
                recyclerView.translationY += sign * recyclerView.width * deltaDistance * OverscrollTranslationMagnitude
            else
                recyclerView.translationX += sign * recyclerView.height * deltaDistance * OverscrollTranslationMagnitude
            animation.cancel()
        }

        override fun onRelease() {
            super.onRelease()
            if (isVertical) {
                if (recyclerView.translationY != 0f)
                    animation.start()
            } else {
                if (recyclerView.translationX != 0f)
                    animation.start()
            }
        }

        override fun onAbsorb(velocity: Int) {
            super.onAbsorb(velocity)
            val translationVelocity = sign * velocity * FlingTranslationMagnitude
            animation.cancel()
            animation.setStartVelocity(translationVelocity)
            animation.start()
        }

        override fun draw(canvas: Canvas?): Boolean = false

        override fun isFinished(): Boolean = !animation.isRunning
    }
}