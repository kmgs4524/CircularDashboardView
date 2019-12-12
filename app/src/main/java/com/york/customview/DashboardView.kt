package com.york.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout

/**
 * @author MP_User
 * created on 2019/12/12
 */
class DashboardView: FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initView()
    }

    private fun initView() {
        val circleMetersView = CircleMetersView(context)
        addView(circleMetersView)
    }

    // override fun onDraw(canvas: Canvas?) {
    //
    // }

    inner class CircleMetersView : View {

        private var paintWidth = 0
        private var paintHeight = 0
        private var radius = 0
        private val paint = Paint()
        private val center = Point()

        constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

        constructor(context: Context): super(context)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            paintWidth = MeasureSpec.getSize(widthMeasureSpec)
            paintHeight = MeasureSpec.getSize(heightMeasureSpec)
        }

        override fun onDraw(canvas: Canvas?) {
            // 初始化中心點
            center.x = width / 2
            center.y = height / 2
            // 初始化畫外圈所需元件
            val matrix = Matrix()
            paint.color = Color.LTGRAY
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f
            val path = Path()

            radius = center.x - (paintWidth / 5) + DensityUtils.convertDpToPixel(7, context).toInt()
            var startDegree = 180f
            var endDegree = 200f
            var currentDegree = startDegree

            while (currentDegree <= endDegree) {
                matrix.setRotate(currentDegree)
                matrix.postTranslate(center.x.toFloat(), center.y.toFloat())
                path.transform(matrix)
                path.moveTo(radius.toFloat(), 0f)
                drawInnerMeters(canvas!!, paint, matrix)
                path.lineTo(radius.toFloat() + convertDpToPx(10), 0f)
                canvas!!.drawPath(path, paint)
                currentDegree += 5.625f
                Log.d(TAG, "currentDegree: $currentDegree")
            }
        }

        private fun drawInnerMeters(canvas: Canvas, paint: Paint, matrix: Matrix) {
            val path = Path()

            path.transform(matrix)
            path.lineTo(radius + convertDpToPx(7), 0f)
            canvas.drawPath(path, paint)
        }
    }

    private fun convertDpToPx(dp: Int): Float {
        return DensityUtils.convertDpToPixel(dp, context)
    }

    companion object {
        const val TAG = "DashboardView"
    }

}