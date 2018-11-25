package com.lazard.nyapp.nyapp.domain

import android.content.Context
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.model.StickerItem

class StickersFactory(val context: Context) {
    fun getStickers() : List<StickerGroup> {
        return context.assets.list("groups").map { StickerGroup(it) }.map { group ->
            context.assets.list("stickers/${group.dir}").forEach {
                group.items.add(StickerItem(it, group))
            }
            group
        }
    }
}