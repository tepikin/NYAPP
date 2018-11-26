package com.lazard.nyapp.nyapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.TypedValue
import java.io.File


object Utils{

    fun convertToMutable(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        if (bitmap.isRecycled) return bitmap
        if (bitmap.isMutable) return bitmap

        var config: Bitmap.Config? = bitmap.config
        if (config == null) config = Bitmap.Config.ARGB_8888

        val result = bitmap.copy(config, true)

        if (result == null || result.isRecycled) {
            throw OutOfMemoryError("On ConvertToMutable")
        }

        return result
    }

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

