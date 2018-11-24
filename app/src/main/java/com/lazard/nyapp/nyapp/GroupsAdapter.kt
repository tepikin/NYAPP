package com.lazard.nyapp.nyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GroupsAdapter(context:Context,val listener:(StickerGroup?)->Unit) : RecyclerView.Adapter<GroupsAdapter.Holder>() {
    val layout = LayoutInflater.from(context)
    val items = arrayListOf<StickerGroup>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(layout.inflate(R.layout.group_item,parent,false))
    }

    override fun getItemCount(): Int  =items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(view:View): RecyclerView.ViewHolder(view) {
        var stickerGroup:StickerGroup?=null
        init {
            view .setOnClickListener { listener(stickerGroup) }
        }
        fun bind(stickerGroup: StickerGroup) {
            this.stickerGroup= stickerGroup

        }

    }
}

