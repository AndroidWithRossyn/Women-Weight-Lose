package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.youtube.player.internal.r
import com.loseweight.R
import com.loseweight.objects.CustomGallery
import java.io.File
import java.util.*

class ImageListRecyclerAdapter(private val mContext: Context) :
    RecyclerView.Adapter<ImageListRecyclerAdapter.VerticalItemHolder>() {
    override fun getItemCount(): Int {
        return data.size
    }

    var data = ArrayList<CustomGallery>()
    var isMultiSelected: Boolean = false
        private set

    var mEventListener: EventListener? = null

    val selected: ArrayList<CustomGallery>
        get() {
            val dataT = ArrayList<CustomGallery>()
            for (i in data.indices) {
                if (data[i].isSeleted) {
                    dataT.add(data[i])
                }
            }

            return dataT
        }


    val totalSelected: Int
        get() {
            var totalSelected = 0
            for (i in data.indices) {
                if (data[i].isSeleted) {
                    totalSelected += 1
                }
            }
            return totalSelected
        }


    interface EventListener {
        fun onItemClickListener(position: Int)
    }

    fun addAll(files: ArrayList<CustomGallery>) {
        try {
            this.data.clear()
            this.data.addAll(files)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        notifyDataSetChanged()

    }

    fun isSelected(position: Int): Boolean {
        return data[position].isSeleted
    }

    fun changeSelection(position: Int) {
        data[position].isSeleted = !data[position].isSeleted
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun setMultiplePick(isMultiplePick: Boolean) {
        this.isMultiSelected = isMultiplePick
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): VerticalItemHolder {
        val inflater = LayoutInflater.from(container.context)
        val root = inflater.inflate(R.layout.item_custom_gallery, container, false)
        return VerticalItemHolder(root)
    }

    override fun onBindViewHolder(holder: VerticalItemHolder, position: Int) {
        val item = data[position]
        holder.setImage(item.sdcardPath)
//        if (isMultiSelected) {
//            holder.imgSelected.setVisibility(View.VISIBLE);
//        } else {
//            holder.imgSelected.setVisibility(View.GONE);
//        }

//        holder.imgSelected
//            .setSelected(item.isSeleted);
        holder.imgSelected!!.visibility = if (item.isSeleted) View.VISIBLE else View.GONE
//        if (item.isSeleted) {
//            holder.imgSelected.visibility = View.VISIBLE
//        } else {
//            holder.imgSelected.visibility = View.GONE
//        }

        holder.container!!.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClickListener(position)
            }
        }
    }


    fun getItem(position: Int): CustomGallery {
        return data[position]
    }


    inner class VerticalItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgImage: AppCompatImageView = itemView.findViewById<AppCompatImageView>(R.id.imgImage)
        var imgSelected: AppCompatImageView = itemView.findViewById<AppCompatImageView>(R.id.imgSelected)
        var container: FrameLayout = itemView.findViewById<FrameLayout>(R.id.container)

        fun setImage(url: String) {
            Glide.with(mContext)
                .load(File(url))
                .into(imgImage!!)
        }
    }

    fun setEventListner(eventListner: EventListener) {
        mEventListener = eventListner
    }

}

