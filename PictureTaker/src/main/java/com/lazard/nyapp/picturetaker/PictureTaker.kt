package com.lazard.nyapp.picturetaker

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.database.DatabaseUtils
import android.util.Log


object PictureTaker {
 fun takeFromCamera( activity: AppCompatActivity,
                     listener: ((path: String?) -> Unit)? = null){
     PictureTakerCamera.start(activity,listener)
 }

    fun takeFromGallery( activity: AppCompatActivity,
                        listener: ((path: Uri?) -> Unit)? = null){
        PictureTakerGallery.start(activity,listener)
    }

    fun galleryDecodeBitmap(context:Context,uri:Uri) = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)


}