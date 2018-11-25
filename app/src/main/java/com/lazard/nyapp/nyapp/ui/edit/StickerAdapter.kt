package com.lazard.nyapp.nyapp.ui.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.model.StickerItem
import com.lazard.nyapp.nyapp.util.Utils
import com.squareup.picasso.Picasso

class StickerAdapter(context:Context, val listener:(StickerItem?)->Unit) : RecyclerView.Adapter<StickerAdapter.Holder>() {
    val layout = LayoutInflater.from(context)
    val items = arrayListOf<StickerItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(layout.inflate(R.layout.sticker_item,parent,false))
    }

    override fun getItemCount(): Int  =items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(view:View): RecyclerView.ViewHolder(view) {
        var stickerItem: StickerItem?=null
        init {
            view .setOnClickListener { listener(stickerItem) }
        }
        fun bind(stickerItem: StickerItem) {
            this.stickerItem= stickerItem

            val size = Utils.dpToPx(100f,itemView.context).toInt()
            Picasso.get()
                .load("file:///android_asset/${stickerItem.fullName}")
                .resize(size,size)
                .centerInside()
                .into(itemView as ImageView)
        }

    }
}

