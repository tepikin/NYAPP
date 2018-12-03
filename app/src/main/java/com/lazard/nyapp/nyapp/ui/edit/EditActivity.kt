package com.lazard.nyapp.nyapp.ui.edit

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crash.FirebaseCrash
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.model.StickerItem
import com.lazard.nyapp.nyapp.ui.BaseActivity
import com.lazard.nyapp.nyapp.ui.edit.stickers.ApplyStickers
import com.lazard.nyapp.nyapp.util.applicationName
import com.lazard.nyapp.nyapp.util.copyAndClose
import com.lazard.nyapp.picturetaker.getFileProviderUri
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException


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
    val bitmap get() = mainImageView.bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logAnalytics("start","edit","edit")
        setContentView(R.layout.activity_edit)
        mainImageView.doOnLayout { loadImage() }
        initRecyclers()

        saveView.setOnClickListener { saveBitmap() }
        shareView.setOnClickListener { shareBitmap() }

    }

    private fun createShareIntent(action:String) {
        CheckPermissions(this).checkPermissions {
            uiScope.launch {

                val progressDialog = ProgressDialog.show(this@EditActivity,getString(R.string.save_file_progress_dialog_title),getString(R.string.save_file_progress_dialog_message),true,false)
                progressDialog.show()
                try {
                    val uri = bgScope.async { saveBitmapLocal() }.await()
                    val intent = Intent()
                    intent.action = action
                    intent.setDataAndType(uri, "image/jpg")
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }catch (e:Throwable){
                    e.printStackTrace()
                    if (e.toString().contains("No space left on device")){
                        toast(R.string.save_file_failed_no_space)
                    }else {
                        toast(R.string.save_file_failed)
                    }
                    FirebaseCrash.log("Save file failed")
                }
                progressDialog.hide()
            }
        }
    }

    private fun shareBitmap() {
        logAnalytics("share","save","edit")
        createShareIntent(Intent.ACTION_SEND)
    }


    private fun saveBitmap() {
        logAnalytics("gallery","save","edit")
        createShareIntent(Intent.ACTION_VIEW)
    }

    private fun saveBitmapLocal(): Uri? {
        val externalFile =File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/${this.applicationName}/IMG_${System.currentTimeMillis()}.jpg")
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
        logAnalytics("select","group","edit")
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
        srickersRecyclerView.scrollToPosition(0)
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
        logAnalytics("select","sticker","edit")
        mainImageView.addControllerAsynch(stickerItem?.fullName, -1, null)
    }

    private fun loadImage() {
        try {
            uiScope.launch {
                intent?.data?.let {
                    bgScope.async {contentResolver.openInputStream(it)?.copyAndClose(tempImageFile)
                    }.await()
                }
                logAnalytics("copy","load","edit")

                Picasso.get()
                    .load(tempImageFile)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(mainImageView.width, mainImageView.height)
                    .centerInside()
                    .into(mainImageView)
                logAnalytics("loaded","load","edit")
            }
        } catch (e: Throwable) {
            cantLoadImage()
            FirebaseCrash.log("Load file failed")
            logAnalytics("error","load","edit")
        }
    }


    private fun cantLoadImage() {
        Toast.makeText(this, R.string.load_file_failed, Toast.LENGTH_SHORT).show()
        finish()
    }

    fun hideWater() {

    }

    fun showWater() {

    }

}
