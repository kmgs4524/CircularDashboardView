package com.york.customview
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class GradientView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val paint = Paint()

    override fun onDraw(canvas: Canvas) {
//        paint.shader = LinearGradient(
//            convertDpToPx(0),
//            convertDpToPx(0),
//            measuredWidth.toFloat(),
//            convertDpToPx(0),
//            intArrayOf(Color.BLACK, Color.YELLOW, Color.RED),
//            floatArrayOf(0f, 0.3f, 1.0f),
//            Shader.TileMode.CLAMP
//        )
        paint.shader = LinearGradient(0f,
            0f,
            measuredWidth.toFloat(),   // center.x.toFloat()
            0f,
            intArrayOf(Color.parseColor("#77c7c8"), Color.parseColor("#0070ba")),
            null,
            Shader.TileMode.CLAMP)

        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)

    }

    private fun convertDpToPx(dp: Int): Float {
        return com.york.circulardashboardview.DensityUtils.convertDpToPixel(dp, context)
    }
}