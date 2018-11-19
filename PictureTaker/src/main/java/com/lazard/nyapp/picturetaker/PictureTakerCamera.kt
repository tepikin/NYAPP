package com.lazard.nyapp.picturetaker

import android.app.Activity
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

class PictureTakerCamera : androidx.fragment.app.Fragment() {

    companion object {

        fun start(
            activity: AppCompatActivity,
            listener: ((path: String?) -> Unit)? = null
        ) {
            val pictureTaker2 = PictureTakerCamera()
            pictureTaker2.listener = { path: String? ->
                try {
                    listener?.invoke(path)
                } finally {
                    activity.supportFragmentManager?.transaction { remove(pictureTaker2) }
                }
            }
            activity.supportFragmentManager?.transaction(true, false) { add(pictureTaker2, "PictureTakerPhoto") }
        }
    }

    private val requestTakePhotoId: Int = (1..9999).random()
    private var listener: ((path: String?) -> Unit)? = null
    var mCurrentPhotoPath: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dispatchTakePictureIntent()
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }


    private fun dispatchTakePictureIntent() {
        val activityInst = activity
        activityInst ?: return
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activityInst.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri? = FileProvider.getUriForFile(
                        activityInst,
                        "${activityInst.packageName}.picturetaker.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, requestTakePhotoId)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestTakePhotoId && resultCode == Activity.RESULT_OK) {
            listener?.invoke(mCurrentPhotoPath)
        }
    }


}