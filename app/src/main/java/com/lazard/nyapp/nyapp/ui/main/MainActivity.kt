package com.lazard.nyapp.nyapp.ui.main

import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import androidx.core.view.doOnLayout
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.ui.BaseActivity
import com.lazard.nyapp.nyapp.ui.edit.EditActivity
import com.lazard.nyapp.picturetaker.PictureTaker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        cameraSmall?.doOnLayout {
            frontImage?.imageMatrix?.apply {

                fun mapView(size:Pair<Int,Int>,center:Point,rotate:Float,view:View){
                    val (width,height) = size
                    val rect = Rect(center.x-width/2, center.y-height/2, center.x-width/2+width, center.y-height/2+height).toRectF()
                    mapRect(rect)
                    view.setViewByRect(rect)
                    view.rotation = rotate
                }

                mapView(234 to 234,Point(635,1187),26f,galleryBig)
                mapView(85 to 85,Point(363,767),19.59f,gallerySmall)

                mapView(173 to 173,Point(390,940),-18.04f,cameraBig)
                mapView(65 to 65,Point(233,707),22.66f,cameraSmall)

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