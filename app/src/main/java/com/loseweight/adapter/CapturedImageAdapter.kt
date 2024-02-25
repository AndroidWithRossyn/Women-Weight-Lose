package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loseweight.R
import com.loseweight.databinding.ItemCapturedImagesBinding
import com.loseweight.objects.CustomGallery
import com.loseweight.utils.Utils

class CapturedImageAdapter(internal var context: Context) :
    RecyclerView.Adapter<CapturedImageAdapter.MyViewHolder>() {

    private var data: MutableList<CustomGallery>? = mutableListOf()
    private lateinit var mEventListener: EventListener


    fun add(mData: CustomGallery) {
        data!!.add(mData)
        notifyDataSetChanged()
    }

    fun addAll(mData: MutableList<CustomGallery>) {
        try {
            this.data!!.addAll(mData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }

    fun clear() {
        data!!.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinder = DataBindingUtil.inflate<ItemCapturedImagesBinding>(
            inflater,
            R.layout.item_captured_images, parent, false
        )
        return MyViewHolder(itemBinder)
    }

    fun getItem(pos: Int): CustomGallery {
        return data!!.get(pos)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemBinder.imgCancel.setOnClickListener {
            mEventListener.onItemCancelClicked(position)
        }
        if (item.isRes) {
            Utils.loadImage(holder.itemBinder.imgProductPhoto,item.sdcardPath,context,R.mipmap.placeholder)
        } else {
            Glide.with(context)
                .load(item.sdcardPath)
                .into(holder.itemBinder.imgProductPhoto!!)
        }

    }

    override fun getItemCount(): Int {
        return data!!.size
    }
    fun getAll(): MutableList<CustomGallery> {
        return data!!
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }

    fun remove(position : Int){
        data!!.removeAt(position)
        notifyItemRemoved(position)
    }

    interface EventListener {
        fun onItemViewClicked(position: Int)
        fun onItemCancelClicked(position: Int)
    }

    inner class MyViewHolder(internal var itemBinder: ItemCapturedImagesBinding) :
        RecyclerView.ViewHolder(itemBinder.root)
}
