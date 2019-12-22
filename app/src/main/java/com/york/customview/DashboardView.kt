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
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import timber.log.Timber

/**
 * @author MP_User
 * created on 2019/12/12
 */
class DashboardView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.DashboardView,
            0,
            0
        ).apply {
            try {
                val labelText = getString(R.styleable.DashboardView_labelText) ?: ""
                initView(labelText)
            } finally {
                recycle()
            }
        }
    }

    private fun initView(labelText: String = "") {
        val circleMetersView = CircleMetersView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tag = "CIRCLE_METERS"
        }
        val percentageText = AppCompatTextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tag = "PERCENTAGE_TEXT"
            textSize = 24f
            setTextColor(Color.WHITE)
            text = "Hello"
        }
        val labelTextView = AppCompatTextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tag = "LABEL_TEXT"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            text = labelText
        }
        addView(circleMetersView)
        addView(percentageText)
        addView(labelTextView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Timber.d(
            "changed: $changed left: ${convertPxToDp(left)} top: ${convertPxToDp(top)} " +
                    "right: ${convertPxToDp(right)} bottom: ${convertPxToDp(bottom)}"
        )

        val circleMetersView = findViewWithTag<CircleMetersView>("CIRCLE_METERS")
        circleMetersView.layout(
            0,
            (measuredHeight / 2) - (circleMetersView.measuredHeight / 2),
            circleMetersView.measuredWidth,
            (measuredHeight / 2) + (circleMetersView.measuredHeight / 2)
        )

        // Set percentage text at center of parent view
        val percentageText = findViewWithTag<TextView>("PERCENTAGE_TEXT")
        val percentageTextBottom = (measuredHeight / 2) + (percentageText.measuredHeight / 2)
        percentageText.layout(
            (measuredWidth / 2) - (percentageText.measuredWidth / 2),
            measuredHeight / 2 - (percentageText.measuredHeight / 2),
            (measuredWidth / 2) + (percentageText.measuredWidth / 2),
            percentageTextBottom
        )
        // Set label text under percentage text
        val labelText = findViewWithTag<TextView>("LABEL_TEXT")
        val labelTextTop = percentageTextBottom
        val labelTextBottom = labelTextTop + labelText.measuredHeight
        labelText.layout(
            (this.measuredWidth / 2) - (labelText.measuredWidth / 2),
            labelTextTop,
            (this.measuredWidth / 2) + (labelText.measuredWidth / 2),
            labelTextBottom
        )


        Timber.d("child text: $percentageText tag: ${percentageText.tag}")
    }

    inner class CircleMetersView : View {

        private var paintWidth = 0
        private var paintHeight = 0
        private var radius = 0
        private val center = Point()

        private var drawMatrix = Matrix()

        constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

        constructor(context: Context) : super(context)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            paintWidth = MeasureSpec.getSize(widthMeasureSpec)
            paintHeight = MeasureSpec.getSize(heightMeasureSpec)
        }

        private val path = Path()
        private val paint = Paint()

        override fun onDraw(canvas: Canvas?) {
            val canvas = canvas ?: return

            // 初始化畫布背景色
            paint.style = Paint.Style.FILL
            paint.color = Color.BLUE
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            Log.d(
                this.javaClass.simpleName,
                "width: ${DensityUtils.convertPixelToDp(
                    width.toFloat(),
                    context
                )} height: ${DensityUtils.convertPixelToDp(height.toFloat(), context)}"
            )
            // 初始化中心點
            center.x = width / 2
            center.y = height / 2
            // 初始化畫外圈所需元件
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f

            // val drawMatrix = Matrix()
            // 試畫線
//             path.moveTo(center.x.toFloat(), center.y.toFloat())
//             path.lineTo(100f, 50f)
            // val cx = width.toFloat() / 2
            // val cy = height.toFloat() / 2
            // drawMatrix.postTranslate(cx, cy)
            // path.transform(drawMatrix)
            // canvas.drawPath(path, paint)
            // paint.color = Color.BLACK
            // paint.strokeWidth = 8f
            // path.lineTo(200f, 200f)
            // drawMatrix.postTranslate(-cx, -cy)
            // path.transform(drawMatrix)
            // canvas.drawPath(path, paint)

            radius = center.x - (paintWidth / 5) + DensityUtils.convertDpToPixel(7, context).toInt()
            var startDegree = 165f  // 165f
            var endDegree = 380f
            var currentDegree = startDegree

            while (currentDegree <= endDegree) {
                drawMatrix.setRotate(currentDegree)
                drawMatrix.postTranslate(center.x.toFloat(), center.y.toFloat())
                path.moveTo(radius.toFloat(), 0f)
                setPaintInnerStyle()
                drawInnerMeters(canvas, paint, path, drawMatrix)
                setPaintOuterStyle()
                drawOuterMeters(canvas, paint, path, drawMatrix)
                canvas.drawPath(path, paint)
                path.reset()
                currentDegree += 5.625f
                Log.d(TAG, "currentDegree: $currentDegree")
            }
        }

        private fun drawInnerMeters(canvas: Canvas, paint: Paint, path: Path, matrix: Matrix) {
            path.lineTo(radius + convertDpToPx(7), 0f)
            path.transform(matrix)
            canvas.drawPath(path, paint)
            path.reset()
        }

        private fun drawOuterMeters(canvas: Canvas, paint: Paint, path: Path, matrix: Matrix) {
            val originPoint = radius + convertDpToPx(9)
            path.moveTo(originPoint, 0f)
            path.lineTo(originPoint + convertDpToPx(24), 0f)
            path.transform(matrix)
            canvas.drawPath(path, paint)
            path.reset()
        }

        private fun setPaintInnerStyle() {
            paint.color = Color.LTGRAY
            paint.strokeWidth = 4f
        }

        private fun setPaintOuterStyle() {
            paint.color = Color.LTGRAY
            paint.strokeWidth = 8f
        }
    }

    private fun convertDpToPx(dp: Int): Float {
        return DensityUtils.convertDpToPixel(dp, context)
    }

    private fun convertPxToDp(px: Int): Int {
        return DensityUtils.convertPixelToDp(px.toFloat(), context).toInt()
    }

    companion object {
        const val TAG = "DashboardView"
    }

}