package com.lazard.nyapp.nyapp.domain

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.model.StickerItem

class StickersFactory(val context: Context) {
    fun getStickers() : List<StickerGroup> {
        val toJson = context.assets.open("stickers.json").bufferedReader().readText()
        val groups = Gson().fromJson<List<StickerGroup>>(toJson, object : TypeToken<List<StickerGroup>>() {}.type)
        return groups
    }
}