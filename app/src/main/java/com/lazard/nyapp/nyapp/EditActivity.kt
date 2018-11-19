package com.lazard.nyapp.nyapp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lazard.nyapp.picturetaker.PictureTaker
import kotlinx.android.synthetic.main.activity_edit.*



class EditActivity : AppCompatActivity() {
    companion object {
        fun show(photoUri: Uri?, context: Context) {
            Intent(context, EditActivity::class.java).apply {
                data = photoUri
                context.startActivity(this)
            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        intent?.data?.let {
            val input = contentResolver.openInputStream(it).use {
                mainImage.setImageBitmap(BitmapFactory.decodeStream(it))}
        } ?: cantLoadIamge()
    }

    private fun cantLoadIamge() {
        Toast.makeText(this, "Can't Load Image", Toast.LENGTH_SHORT).show()
        finish()
    }

}
