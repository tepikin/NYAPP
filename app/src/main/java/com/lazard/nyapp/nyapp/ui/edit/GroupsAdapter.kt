package com.lazard.nyapp.nyapp.ui.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lazard.nyapp.nyapp.R
import com.lazard.nyapp.nyapp.domain.StickersFactory
import com.lazard.nyapp.nyapp.model.StickerGroup
import com.lazard.nyapp.nyapp.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit.*

class GroupsAdapter(context:Context,val listener:(StickerGroup?)->Unit) : RecyclerView.Adapter<GroupsAdapter.Holder>() {
    val layout = LayoutInflater.from(context)
    val items = mutableListOf<StickerGroup>()
    fun getSelectedGroup() = items.find { it.isSelected }
    fun setSelectedGroup(group:StickerGroup) {items.forEach{ it.isSelected=false };group.isSelected=true}

    init {
        items.addAll(StickersFactory(context).getStickers())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(layout.inflate(R.layout.group_item,parent,false))
    }

    override fun getItemCount(): Int  =items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(view:View): RecyclerView.ViewHolder(view) {
        var stickerGroup: StickerGroup?=null
        init {
            view .setOnClickListener { listener(stickerGroup) }
        }
        fun bind(stickerGroup: StickerGroup) {
            this.stickerGroup= stickerGroup
            itemView.isSelected=stickerGroup.isSelected
            val size = Utils.dpToPx(48f,itemView.context).toInt()
            Picasso.get()
                .load("file:///android_asset/${stickerGroup.fullName}")
                .resize(size,size)
                .centerInside()
                .into(itemView as ImageView)
        }

    }
}

