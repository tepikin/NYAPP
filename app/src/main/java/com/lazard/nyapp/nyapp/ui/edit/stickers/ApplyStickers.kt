package com.lazard.nyapp.nyapp.ui.edit.stickers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView.*
import com.lazard.nyapp.nyapp.util.BitmapUtils
import com.lazard.nyapp.nyapp.util.recycleSafe
import java.io.File
import java.util.*

class ApplyStickers {
    fun saveBitmap(mainImageView: ImageViewRotate, origFile: File, targetFile: File) {
        val stickersAction = createStickersAction(mainImageView)

        val decodedBigestBitmap = BitmapUtils.decodeBigestBitmap(origFile)
        val result = stickersAction.apply(decodedBigestBitmap)
        if (decodedBigestBitmap!=result)decodedBigestBitmap?.recycleSafe()
        targetFile.parentFile.apply { mkdir();mkdirs() }
        targetFile.createNewFile()
        result?.compress(Bitmap.CompressFormat.JPEG, 80, targetFile.outputStream())
        result?.recycleSafe()
    }

    private fun createStickersAction(mainImageView: ImageViewRotate): StickersAction {
        val context = mainImageView.context
        val bitmap = mainImageView.bitmap
        val canvas = Canvas(bitmap)

        val stickerItems = ArrayList<StickersAction.StickerItem>()

        for (controller in mainImageView.getControllers()) {
            //RectF = controller.getItem().getImageRect();

            val points = controller.getItem().getDrawRect01(bitmap.width, bitmap.height)
            val item = StickersAction.StickerItem()
            item.setPoints(points)
            if (controller.getItem() is PngItem) {
                item.setType(StickersAction.StickerItem.Type.png)
                item.setName((controller.getItem() as PngItem).getName())
                item.setInitRect((controller.getItem() as PngItem).getImageRect())
            } else if (controller.getItem() is SvgItem) {
                item.setType(StickersAction.StickerItem.Type.svg)
                item.setName((controller.getItem() as SvgItem).getName())
                item.setColor((controller.getItem() as SvgItem).getColor())
                item.setInitRect((controller.getItem() as SvgItem).getImageRect())
            } else if (controller.getItem() is TextControllerItem) {
                item.setType(StickersAction.StickerItem.Type.text)
                item.setColor((controller.getItem() as TextControllerItem).getColor())
                item.setTextModel((controller.getItem() as TextControllerItem).getTextModel())
                item.setInitRect((controller.getItem() as TextControllerItem).getImageRect())
            }
            stickerItems.add(item)


            controller.getItem().draw(canvas)

        }

        val stickersAction = StickersAction(context, stickerItems)
        stickersAction.setContext(context)
        return stickersAction
    }
}