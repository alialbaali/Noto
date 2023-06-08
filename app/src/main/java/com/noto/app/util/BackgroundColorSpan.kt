package com.noto.app.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import android.util.TypedValue
import kotlin.math.roundToInt

class CustomBackgroundColorSpan(
    private val context: Context,
    private val backgroundColor: Int,
    private val foregroundColor: Int,
    private val textHeight: Float,
) : ReplacementSpan() {

    private val verticalPadding = context.pixelsOf(TypedValue.COMPLEX_UNIT_DIP, 8F)
    private val radius = context.pixelsOf(TypedValue.COMPLEX_UNIT_DIP, 6F)

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return paint.measureText(text, start, end).roundToInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val halfTextHeight = textHeight / 2
        val rect = RectF(x, y - halfTextHeight - verticalPadding, x + paint.measureText(text, start, end), y + verticalPadding)
        paint.color = backgroundColor
        canvas.drawRoundRect(rect, radius, radius, paint)
        paint.color = foregroundColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}