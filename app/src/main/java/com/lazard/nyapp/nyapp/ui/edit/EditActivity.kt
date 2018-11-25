package com.lazard.nyapp.nyapp.ui.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.util.copyAndClose
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.model.StickerItem
import com.lazard.nyapp.nyapp.ui.BaseActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


class EditActivity : BaseActivity() {
    companion object {
        fun show(photoUri: Uri?, context: Context) {
            Intent(context, EditActivity::class.java).apply {
                data = photoUri
                context.startActivity(this)
            }
        }
    }

    val tempImageFile = File("tempImage")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        mainImageView.doOnLayout {loadImage()}
        initRecyclers()

    }

    private fun initRecyclers() {
        groupsRecyclerView.layoutManager = LinearLayoutManager(this)
        groupsRecyclerView.adapter = GroupsAdapter(this, ::onGroupClick)
        groupsRecyclerView.setHasFixedSize(true)
        srickersRecyclerView.layoutManager = LinearLayoutManager(this)
        srickersRecyclerView.adapter = StickerAdapter(this,::onStickerClick)
        srickersRecyclerView.setHasFixedSize(true)
    }

    private fun onGroupClick(stickerGroup: StickerGroup?) {}
    private fun onStickerClick(stickerGroup: StickerItem?) {}

    private fun loadImage() {
        try {
            uiScope.launch {
                intent?.data?.let {bgScope.async { contentResolver.openInputStream(it)?.copyAndClose(tempImageFile) }.await()}

                Picasso.get()
                    .load(tempImageFile)
                    .centerInside()
                    .into(mainImageView)
            }
        } catch (e: Throwable) {
            cantLoadImage()
        }
    }


    private fun cantLoadImage() {
        Toast.makeText(this, "Can't Load Image", Toast.LENGTH_SHORT).show()
        finish()
    }

}
