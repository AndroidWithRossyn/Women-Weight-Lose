package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.R
import com.loseweight.databinding.ItemRecentBinding
import com.loseweight.objects.HistoryDetailsClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import kotlin.collections.ArrayList


class RecentAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecentAdapter.MyViewHolder>(){

    private val data = mutableListOf<HistoryDetailsClass>()
    internal var mEventListener: EventListener? = null
    private val dbHelper = DataHelper(context)

    fun getItem(pos: Int): HistoryDetailsClass {
        return data[pos]
    }

    fun addAll(mData: ArrayList<HistoryDetailsClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: HistoryDetailsClass) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }


    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemRecentBinding>(
            inflater,
            R.layout.item_recent, parent, false)
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = getItem(position)

        holder.rowSideMenuBinding.tvRecentWorkOutName.text = item.PlanName
        holder.rowSideMenuBinding.imgRecentWorkout.setImageResource(Utils.getDrawableId(item.planDetail!!.planThumbnail,context))


        if (item.planDetail!!.planDays.equals(Constant.PlanDaysYes))
        {
            val compDay =dbHelper.getCompleteDayCountByPlanId(item.planDetail?.planId!!)
            holder.rowSideMenuBinding.tvTime.text = (item.planDetail?.days!!.toInt() - compDay).toString().plus(" Days left")

        }else{
            holder.rowSideMenuBinding.tvTime.text = item.planDetail!!.planMinutes.plus(" Mins")
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

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemRecentBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
