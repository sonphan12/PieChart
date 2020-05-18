package com.example.piechart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.random.Random

class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius: Float = UNSPECIFIED
    private val paint: Paint by lazy {
        createStrokePaint()
    }
    private val oval = RectF()
    private val slices: MutableList<Slice> = ArrayList()
    private val colors: MutableList<Int> = ArrayList()

    private fun createStrokePaint(): Paint =
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            strokeWidth = 5f
        }

    override fun onDraw(canvas: Canvas) {
        if (radius == UNSPECIFIED) {
            radius = min(width, height).toFloat() / 2
        }
        slices.forEach { drawSlice(canvas, it) }
    }

    private fun drawSlice(canvas: Canvas, slice: Slice) {
        oval.set(
            paint.strokeWidth,
            paint.strokeWidth,
            radius * 2 - paint.strokeWidth,
            radius * 2 - paint.strokeWidth
        )
        slice.draw(oval, canvas, paint)
    }

    fun setRatios(ratios: List<Float>) {
        validateRatios(ratios)
        this.slices.run {
            clear()
            addAll(toSlice(ratios))
        }
        applyColors()
        invalidate()
        requestLayout()
    }

    fun setColors(colors: List<Int>) {
        this.colors.run {
            clear()
            addAll(colors)
        }
        applyColors()
        invalidate()
        requestLayout()
    }

    private fun applyColors() {
        if (colors.size < slices.size) {
            colors.clear()
            // Random colors
            repeat(slices.size) {
                colors.add(generateRandomColor())
            }
        }
        val slicesSnapShot = ArrayList(slices)
        slices.clear()
        slicesSnapShot.forEachIndexed { index, slice ->
            slices.add(Slice(slice.startAngle, slice.sweepAngle, colors[index]))
        }
    }

    private fun generateRandomColor(): Int = Random.run {
        Color.argb(255, nextInt(256), nextInt(256), nextInt(256))
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

    private class Slice(
        internal val startAngle: Float,
        internal val sweepAngle: Float,
        internal val color: Int = Color.BLACK
    ) {
        internal fun draw(oval: RectF, canvas: Canvas, paint: Paint) {
            val oldColor = paint.color
            paint.color = color
            val count = canvas.save()
            canvas.drawArc(oval, startAngle, sweepAngle, true, paint)
            canvas.restoreToCount(count)
            paint.color = oldColor
        }
    }
}