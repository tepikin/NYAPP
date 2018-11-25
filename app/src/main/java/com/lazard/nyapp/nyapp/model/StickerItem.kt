package com.lazard.nyapp.nyapp.model

class StickerItem(val name:String,var group : StickerGroup? = null){
val fullName = "stickers/${group?.dir}/${name}"
}