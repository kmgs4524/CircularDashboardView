package com.york.circulardashboardview

import android.content.Context

/**
 * @author MP_User
 * created on 2019/12/12
 */
object DensityUtils {

    fun convertPixelToDp(px: Float, context: Context): Float {
        return px / getDensity(context)
    }

    fun convertDpToPixel(dp: Int, context: Context): Float {
        return dp * getDensity(context)
    }

    private fun getDensity(context: Context): Float {
        val matrix = context.resources.displayMetrics
        return matrix.density
    }
}