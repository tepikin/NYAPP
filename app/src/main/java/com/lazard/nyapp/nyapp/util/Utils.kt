package com.lazard.nyapp.nyapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.TypedValue



object Utils{
    fun dpToPx(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,context.displayMetrics)
    }

    fun pxToDp(pix: Float, context: Context): Float {
        return pix / context.displayMetrics.density
    }

    fun resycle(bitmap: Bitmap?) {
        bitmap?.recycleSafe()
    }

    fun getRotate(matrix: Matrix): Double {
        val m = FloatArray(10)
        matrix.getValues(m)
        return Math.atan2(m[1].toDouble(), m[0].toDouble())
    }

    fun vectorNormal(vector: DoubleArray): DoubleArray {
        val length = vectorLength(vector)
        return doubleArrayOf(vector[0] / length, vector[1] / length)
    }

    fun vectorLength(vector: DoubleArray): Double {
        return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1])
    }

}

