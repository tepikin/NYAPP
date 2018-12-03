package com.lazard.nyapp.picturetaker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import android.os.Build
import com.lazard.picturetaker.R


class PictureTakerGallery : androidx.fragment.app.Fragment() {

    companion object {

        fun start(
            activity: AppCompatActivity,
            listener: ((uri: Uri?) -> Unit)? = null
        ) {
            val pictureTaker2 = PictureTakerGallery()
            pictureTaker2.listener = { uri: Uri? ->
                try {
                    listener?.invoke(uri)
                } finally {
                    activity.supportFragmentManager?.transaction { remove(pictureTaker2) }
                }
            }
            activity.supportFragmentManager?.transaction(true, false) { add(pictureTaker2, "PictureTakerGallery") }
        }
    }

    private val GALLERY_INTENT_CALLED: Int = (1..9999).random()
    private val GALLERY_KITKAT_INTENT_CALLED: Int = GALLERY_INTENT_CALLED + 1
    private var listener: ((uri: Uri?) -> Unit)? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val intent = Intent()
            intent.type = "image/jpeg"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, context?.getString(R.string.load_file_prompt)), GALLERY_INTENT_CALLED)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/jpeg"
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        data ?: return
        var originalUri: Uri? = null
        if (requestCode == GALLERY_INTENT_CALLED) {
            originalUri = data.getData();
            listener?.invoke(originalUri)
        } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
            originalUri = data.getData();
            var takeFlags =
                data.getFlags() and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity?.getContentResolver()?.takePersistableUriPermission(originalUri, takeFlags)
            }
            listener?.invoke(originalUri)
        }
    }


}


