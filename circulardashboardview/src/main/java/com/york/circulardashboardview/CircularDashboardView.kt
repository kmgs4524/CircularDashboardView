package com.york.circulardashboardview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author MP_User
 * created on 2019/12/12
 */
class CircularDashboardView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CircularDashboardView,
            0,
            0
        ).apply {
            try {
                val percent  = getInt(R.styleable.CircularDashboardView_percent, 0)
                val percentTextSize = getString(R.styleable.CircularDashboardView_percentTextSize) ?: "18sp"
                val labelText = getString(R.styleable.CircularDashboardView_labelText) ?: ""
                val labelTextSize = getString(R.styleable.CircularDashboardView_labelTextSize) ?: "12sp"
                val labelTextColor = getInt(R.styleable.CircularDashboardView_labelTextColor, Color.LTGRAY)
                initView(percent, percentTextSize, labelText, labelTextSize, labelTextColor, attributeSet)
            } finally {
                recycle()
            }
        }
    }

    private fun initView(
        percent: Int = 0,
        percentTextSize: String = "18sp",
        labelText: String = "",
        labelTextSize: String = "",
        labelTextColor: Int = Color.LTGRAY,
        attributeSet: AttributeSet? = null
    ) {
        val circleMetersView = CircleMetersView(context, attributeSet).apply {
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
            // 剔除 sp, dp 單位，只截取數字
            textSize = percentTextSize.substring(0 until percentTextSize.length - 2).toFloat()
            setTextColor(Color.BLACK)
            text = "$percent%"
        }
        val labelTextView = AppCompatTextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tag = "LABEL_TEXT"
            textSize = labelTextSize.substring(0 until labelTextSize.length - 2).toFloat()
            setTextColor(labelTextColor)
            text = labelText
        }
        addView(circleMetersView)
        addView(percentageText)
        addView(labelTextView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG,
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
            // (measuredHeight / 2) - (percentageText.measuredHeight / 2)
            (measuredHeight / 3),
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

        Log.d(TAG, "child text: $percentageText tag: ${percentageText.tag}")
    }

    inner class CircleMetersView : View {

        private var paintWidth = 0
        private var paintHeight = 0
        private var radius = 0
        private val center = Point()

        private var drawMatrix = Matrix()

        private var percent = 0

        constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
            context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.CircularDashboardView,
                0,
                0
            ).apply {
                try {
                    percent = getInt(R.styleable.CircularDashboardView_percent, 0)
                } finally {
                    recycle()
                }
            }
        }

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
//            paint.style = Paint.Style.FILL
//            paint.color = Color.BLUE
//            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

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

            radius = center.x - (paintWidth / 5) + DensityUtils.convertDpToPixel(7, context).toInt()
            val startDegree = 150f
            val endDegree = 390f
            var currentDegree = startDegree

            while (currentDegree <= endDegree) {
                drawMatrix.setRotate(currentDegree)
                drawMatrix.postTranslate(center.x.toFloat(), center.y.toFloat())
                path.moveTo(radius.toFloat(), 0f)
                setPaintInnerStyle()
                drawInnerMeters(canvas, paint, path, drawMatrix, currentDegree)
                setPaintOuterStyle()
                val percentDegree = (150 + percent * 2.4).toInt()
                drawOuterMeters(canvas, path, drawMatrix, currentDegree.toInt(), percentDegree)
                canvas.drawPath(path, paint)
                path.reset()
                currentDegree += 6f
                Log.d(TAG, "currentDegree: $currentDegree")
            }
        }

        private fun drawInnerMeters(canvas: Canvas, paint: Paint, path: Path, matrix: Matrix, degree: Float) {
            if (degree == 180f || degree == 270f || degree == 360f) {
                path.moveTo(radius - 20f, 0f)
            }
            path.lineTo(radius + convertDpToPx(7), 0f)
            path.transform(matrix)
            canvas.drawPath(path, paint)
            path.reset()
        }

        private val paintInProgress by lazy {
            Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 8f
                shader = LinearGradient(0f,
                    0f,
                    measuredWidth.toFloat(),   // center.x.toFloat()
                    0f,
                    intArrayOf(Color.parseColor("#77c7c8"), Color.parseColor("#0070ba")),
                    floatArrayOf(0f, 0.8f),
                    Shader.TileMode.CLAMP)
            }
        }
        private val paintOutProgress = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        private fun drawOuterMeters(
            canvas: Canvas,
            path: Path,
            matrix: Matrix,
            currentDegree: Int,
            percentDegree: Int
        ) {
            val originPoint = radius + convertDpToPx(9)
            path.moveTo(originPoint, 0f)
            path.lineTo(originPoint + convertDpToPx(24), 0f)
            path.transform(matrix)

            if (currentDegree <= percentDegree) {
                canvas.drawPath(path, paintInProgress)
            } else {
                canvas.drawPath(path, paintOutProgress)
            }
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