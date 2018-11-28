package com.lazard.nyapp.nyapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream


fun InputStream.copyAndClose(outputStream: OutputStream) =
    use { input -> outputStream.use { input.copyTo(it);it.flush() } }

fun InputStream.copyAndClose(file: File) = copyAndClose(file.outputStream())

fun Context.displaySize() = resources.displayMetrics.run { widthPixels to heightPixels }

object BitmapUtils {
    fun getBitmapOptions(file: File): BitmapFactory.Options =
        BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, this)
        }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int, scaleX: Int, scaleY: Int): Bitmap {
        val matrix = Matrix()
        matrix.preScale(scaleX.toFloat(), scaleY.toFloat())
        matrix.preRotate(degrees.toFloat())
        if (bitmap.width <= 0 || bitmap.height < 0) {
            Log.e("tag", "size is null")
        }
        val rotated = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix,
            false
        )
        if (bitmap != rotated) {
            bitmap.recycle()
        }
        return rotated
    }

    private fun rotateByExif(path: String, bitmap: Bitmap): Bitmap {
        var orientation = ExifInterface.ORIENTATION_NORMAL
        try {
            val exif = ExifInterface(path)
            orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Throwable) {
            return bitmap
        }

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return rotateBitmap(bitmap, 90, 1, 1)
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return rotateBitmap(bitmap, 180, 1, 1)
        }
        return if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotateBitmap(bitmap, 270, -1, 1)
        } else bitmap
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }


    fun decodeSampledBitmap(file: File, reqWidth: Int, reqHeight: Int): Bitmap {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, this)
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
            BitmapFactory.decodeFile(file.absolutePath, this)
        }
    }

    fun decodeBigestBitmap(file: File): Bitmap? {
        return BitmapFactory.Options().run {
            for (scale in 0..10) {
                try {
                    inSampleSize = scale
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath, this)
                    return rotateByExif(file.absolutePath,bitmap)
                }catch (e:Throwable){
                    e.printStackTrace()
                    System.gc()
                }
            }
            null
        }
    }
}


val Context.displayMetrics get() = this.getResources().getDisplayMetrics()

fun Bitmap?.recycleSafe() {
    if (this?.isRecycled == false) this.recycle()
}

