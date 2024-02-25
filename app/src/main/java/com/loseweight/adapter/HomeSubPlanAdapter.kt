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
import com.loseweight.databinding.ItemBodyFocusBinding
import com.loseweight.databinding.ItemChooseYourPlanBinding
import com.loseweight.databinding.ItemHomeDetailBinding
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils
import kotlin.collections.ArrayList


class HomeSubPlanAdapter(internal var context: Context) :
    RecyclerView.Adapter<HomeSubPlanAdapter.MyViewHolder>() {

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

    fun changeSelection(pos: Int) {
        for (i in data.indices) {
            data[i].isSelected = pos == i
        }

        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemHomeDetailBinding>(
            inflater,
            R.layout.item_home_detail, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        if (item.planThumbnail.isNullOrEmpty().not())
            holder.rowSideMenuBinding.imgIcon.setImageResource(
                Utils.getDrawableId(
                    item.planThumbnail,
                    context
                )
            )
        holder.rowSideMenuBinding.tvName.text = item.planName
        holder.rowSideMenuBinding.tvMinutes.text =
            item.planMinutes + " " + context.getString(R.string.mins)
        holder.rowSideMenuBinding.tvPlanLevel.text = item.planLvl

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemHomeDetailBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
