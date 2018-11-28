package com.lazard.nyapp.nyapp.ui.edit

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.model.StickerItem
import com.lazard.nyapp.nyapp.ui.BaseActivity
import com.lazard.nyapp.nyapp.ui.edit.stickers.ApplyStickers
import com.lazard.nyapp.nyapp.util.copyAndClose
import com.lazard.nyapp.picturetaker.getFileProviderUri
import com.squareup.picasso.MemoryPolicy
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

    val tempImageFile by lazy { File(getFilesDir().absolutePath + "/tempImage") }
    val bitmap get() = (mainImageView.drawable as BitmapDrawable).bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        mainImageView.doOnLayout { loadImage() }
        initRecyclers()

        saveView.setOnClickListener { saveBitmap() }
        shareView.setOnClickListener { shareBitmap() }

    }

    private fun shareBitmap() {
        val uri = saveBitmapLocal()

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }


    private fun saveBitmap() {
        val uri = saveBitmapLocal()
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun saveBitmapLocal(): Uri? {
        val externalFile =
            File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/IMG_${System.currentTimeMillis()}.jpg")
        ApplyStickers().saveBitmap(mainImageView, tempImageFile, externalFile)
        MediaScannerConnection.scanFile(this, arrayOf(externalFile.absolutePath), null, null)
        val uri = externalFile.getFileProviderUri(this)
        return uri
    }

    val groupsAdapter by lazy { GroupsAdapter(this, ::onGroupClick) }
    val stickerAdapter by lazy {
        StickerAdapter(this, ::onStickerClick).apply {
            groupsAdapter.items.firstOrNull()?.items?.apply { items.addAll(this) }
        }
    }

    private fun initRecyclers() {
        groupsRecyclerView.layoutManager =
                LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
        groupsRecyclerView.adapter = groupsAdapter
        groupsRecyclerView.setHasFixedSize(true)
        srickersRecyclerView.layoutManager =
                LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
        srickersRecyclerView.adapter = stickerAdapter
        srickersRecyclerView.setHasFixedSize(true)
    }

    private fun onGroupClick(stickerGroup: StickerGroup?) {
        stickerGroup ?: return
        if (groupsAdapter.getSelectedGroup() == stickerGroup) {
            hideStikersPanel()
            stickerGroup.isSelected = false
            groupsAdapter.notifyDataSetChanged()
            return
        }

        groupsAdapter.setSelectedGroup(stickerGroup)
        groupsAdapter.notifyDataSetChanged()

        showStickersPanel()
        stickerAdapter.items.apply {
            clear()
            addAll(stickerGroup.items)
        }
        stickerAdapter.notifyDataSetChanged()
    }

    private fun showStickersPanel() {
        srickersRecyclerView.animation?.cancel()
        srickersRecyclerView.animate().apply {
            translationY(0f)
            setDuration(200)
            setInterpolator(AccelerateDecelerateInterpolator())
        }.start()
    }

    private fun hideStikersPanel() {
        srickersRecyclerView.animation?.cancel()
        srickersRecyclerView.animate().apply {
            translationY(srickersRecyclerView.height.toFloat())
            setDuration(200)
            setInterpolator(DecelerateInterpolator())
        }.start()
    }

    private fun onStickerClick(stickerItem: StickerItem?) {
        mainImageView.addControllerAsynch(stickerItem?.fullName, -1, null)
    }

    private fun loadImage() {
        try {
            uiScope.launch {
                intent?.data?.let {
                    bgScope.async {contentResolver.openInputStream(it)?.copyAndClose(tempImageFile)
                    }.await()
                }


                Picasso.get()
                    .load(tempImageFile)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(mainImageView.width, mainImageView.height)
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

    fun hideWater() {

    }

    fun showWater() {

    }

}
