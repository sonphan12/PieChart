package com.example.piechart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius: Float = UNSPECIFIED
    private val strokePaint: Paint by lazy {
        createStrokePaint()
    }
    private var center: Point? = null
    private val oval = RectF()
    private val slices: MutableList<Slice> = ArrayList()

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
        slices.forEach { drawSlice(canvas, it) }
    }

    private fun drawSlice(canvas: Canvas, slice: Slice) {
        oval.set(
            strokePaint.strokeWidth,
            strokePaint.strokeWidth,
            radius * 2 - strokePaint.strokeWidth,
            radius * 2 - strokePaint.strokeWidth
        )
        slice.draw(oval, canvas, strokePaint)
    }

    fun setRatios(ratios: List<Float>) {
        validateRatios(ratios)
        this.slices.run {
            clear()
            addAll(toSlice(ratios))
        }
        invalidate()
        requestLayout()
    }

    private fun toSlice(ratios: List<Float>): List<Slice> {
        val slices = ArrayList<Slice>()
        for (i in ratios.indices) {
            slices.add(
                Slice(
                    ratioToDegree(ratios.take(i).fold(0f) { acc, ratio -> acc + ratio }),
                    ratioToDegree(ratios[i])
                )
            )
        }
        return slices
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
            paint.color = oldColor
        }
    }
}