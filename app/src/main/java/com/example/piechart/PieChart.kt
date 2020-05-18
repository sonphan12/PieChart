package com.example.piechart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ratios: MutableList<Float> = mutableListOf()
    private var radius: Float = UNSPECIFIED
    private val strokePaint: Paint by lazy {
        createStrokePaint()
    }
    private var center: Point? = null
    private val oval = RectF()

    private fun createStrokePaint(): Paint =
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

    override fun onDraw(canvas: Canvas) {
        if (radius == UNSPECIFIED) {
            radius = min(width, height).toFloat() / 2
            center = Point(radius.toInt(), radius.toInt())
        }
        for (i in 0 until ratios.size) {
            drawSlice(
                canvas,
                ratioToDegree(ratios[i]),
                ratioToDegree(ratios.take(i).fold(0f) { acc, ratio -> acc + ratio })
            )
        }
    }

    private fun drawSlice(canvas: Canvas, angle: Float, angleOffset: Float) {
        oval.set(
            strokePaint.strokeWidth,
            strokePaint.strokeWidth,
            radius * 2 - strokePaint.strokeWidth,
            radius * 2 - strokePaint.strokeWidth
        )
        canvas.drawArc(oval, angleOffset, angle, true, strokePaint)
    }

    fun setRatios(ratios: List<Float>) {
        validateRatios(ratios)
        this.ratios.run {
            clear()
            addAll(ratios)
        }
        invalidate()
        requestLayout()
    }

    private fun validateRatios(ratios: List<Float>) {
        if (ratios.fold(0f) { x, y -> x + y } != 1f) {
            throw IllegalArgumentException()
        }
    }

    private fun ratioToDegree(ratio: Float): Float = (ratio * 360)

    companion object {
        private const val UNSPECIFIED = -1f
    }

    class Slice(
        private val startAngle: Float,
        private val sweepAngle: Float,
        private val color: Int = Color.BLACK
    ) {
        fun draw(oval: RectF, canvas: Canvas, paint: Paint) {
            val oldColor = paint.color
            paint.color = color
            val count = canvas.save()
            canvas.drawArc(oval, startAngle, sweepAngle, true, paint)
            canvas.restoreToCount(count)
        }
    }
}