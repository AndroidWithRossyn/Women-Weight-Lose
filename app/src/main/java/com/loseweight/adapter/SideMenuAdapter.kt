package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.common.view.CMTextView
import com.loseweight.R
import com.loseweight.objects.SideMenuItem
import com.loseweight.utils.Utils

class SideMenuAdapter() : RecyclerView.Adapter<SideMenuAdapter.ViewHolder>() {

    private var data: MutableList<SideMenuItem> = ArrayList<SideMenuItem>()
    private lateinit var mEventListener: EventListener
    internal var isSelected = false


    interface EventListener {
        fun onItemClick(pos: Int)
    }

    lateinit var context : Context

    constructor(context: Context) : this(){
        this.context = context
    }

    fun addAll(mData: List<SideMenuItem>) {
        try {

            this.data.clear()
            this.data.addAll(mData)


        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }


        notifyDataSetChanged()
    }

    fun add(mData: SideMenuItem) {
        try {
            this.data.add(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }


        notifyDataSetChanged()
    }

    fun getItem(pos: Int): SideMenuItem {
        return data[pos]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.item_side_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = data[position]
        holder.tvMenuName.text = item.name;
        holder.imgMenuIcon.setImageResource(item.icon)

        holder.container.setOnClickListener {
          if (mEventListener != null){
              mEventListener.onItemClick(position)
          }
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view : View)  : RecyclerView.ViewHolder(view) {
        var tvMenuName:TextView = view.findViewById<CMTextView>(R.id.tvMenuName) as TextView
        var imgMenuIcon: AppCompatImageView = view.findViewById<AppCompatImageView>(R.id.imgMenuIcon)
        var container: FrameLayout = view.findViewById<FrameLayout>(R.id.container)

    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }

}