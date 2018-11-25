package com.lazard.nyapp.nyapp.ui.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.model.StickerItem

class StickerAdapter(context:Context, val listener:(StickerItem?)->Unit) : RecyclerView.Adapter<StickerAdapter.Holder>() {
    val layout = LayoutInflater.from(context)
    val items = arrayListOf<StickerItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(layout.inflate(R.layout.group_item,parent,false))
    }

    override fun getItemCount(): Int  =items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(view:View): RecyclerView.ViewHolder(view) {
        var stickerGroup: StickerItem?=null
        init {
            view .setOnClickListener { listener(stickerGroup) }
        }
        fun bind(stickerGroup: StickerItem) {
            this.stickerGroup= stickerGroup

        }

    }
}

