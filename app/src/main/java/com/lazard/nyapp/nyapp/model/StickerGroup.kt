package com.lazard.nyapp.nyapp.model

class StickerGroup(val name:String){
    val dir = name.replace("(.*)\\.[^\\.]*".toRegex(),"$1")
var items = mutableListOf<StickerItem>()
}