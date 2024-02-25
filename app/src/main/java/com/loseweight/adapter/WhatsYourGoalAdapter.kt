package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.R
import com.loseweight.ChooseYourPlanActivity
import com.loseweight.databinding.ItemChooseYourPlanBinding
import com.loseweight.databinding.ItemWhatsYourGoalBinding
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils
import kotlin.collections.ArrayList


class WhatsYourGoalAdapter(internal var context: Context) :
    RecyclerView.Adapter<WhatsYourGoalAdapter.MyViewHolder>(){

    private val data = mutableListOf<HomePlanTableClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): HomePlanTableClass {
        return data[pos]
    }

    fun addAll(mData: ArrayList<HomePlanTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: HomePlanTableClass) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun changeSelection(pos:Int)
    {
        for (i in data.indices)
        {
            data[i].isSelected = pos==i
        }

        notifyDataSetChanged()
    }

    fun changeSelectionByID(id:String)
    {
        for (i in data.indices)
        {
            data[i].isSelected = id==data[i].planId
        }

        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemWhatsYourGoalBinding>(
            inflater,
            R.layout.item_whats_your_goal, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        holder.rowSideMenuBinding.tvTitle.text = item.planName

        if (item.isSelected) {
            holder.rowSideMenuBinding.container.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.primary))
            holder.rowSideMenuBinding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
        }
        else {
            holder.rowSideMenuBinding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.col_whats_your_goal))
            holder.rowSideMenuBinding.container.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.primary_light))
        }

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemWhatsYourGoalBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
