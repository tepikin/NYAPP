package com.lazard.nyapp.nyapp

import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toRectF
import androidx.core.view.doOnLayout
import com.lazard.nyapp.picturetaker.PictureTaker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        cameraSmall?.doOnLayout {
            frontImage?.imageMatrix?.apply {
                var galleryBigRect = Rect(512, 1065, 512 + 233, 1065 + 270).toRectF()
                var gallerySmallRect = Rect(313, 722, 313 + 101, 722 + 96).toRectF()
                var cameraBigRect = Rect(262, 840, 262 + 233, 840 + 242).toRectF()
                var cameraSmallRect = Rect(171, 671, 171 + 108, 671 + 84).toRectF()

                var galleryBigRectT = RectF()
                mapRect(galleryBigRect)
                mapRect(gallerySmallRect)
                mapRect(cameraBigRect)
                mapRect(cameraSmallRect)

                galleryBig.setViewByRect(galleryBigRect)
                gallerySmall.setViewByRect(gallerySmallRect)
                cameraBig.setViewByRect(cameraBigRect)
                cameraSmall.setViewByRect(cameraSmallRect)

            }
        }


        galleryBig.setOnClickListener { PictureTaker.takeFromGallery(this) { onUriSelected(it)}}
        gallerySmall.setOnClickListener { PictureTaker.takeFromGallery(this) { onUriSelected(it) } }
        cameraBig.setOnClickListener { PictureTaker.takeFromCamera(this) { onUriSelected(Uri.fromFile(File(it))) } }
        cameraSmall.setOnClickListener { PictureTaker.takeFromCamera(this) { onUriSelected(Uri.fromFile(File(it))) } }
    }

    fun onUriSelected(uri:Uri?){
        uri?:return
        EditActivity.show(uri,this)
    }

}


private fun View.setViewByRect(rect: RectF) {
    (layoutParams as FrameLayout.LayoutParams).apply {
        width = rect.width().toInt()
        height = rect.height().toInt()
        leftMargin = rect.left.toInt()
        topMargin = rect.top.toInt()
    }
    requestLayout()
}