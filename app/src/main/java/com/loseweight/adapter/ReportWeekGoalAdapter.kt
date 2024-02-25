package com.loseweight.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.R
import com.loseweight.databinding.ItemReportWeekDayBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import java.util.*
import kotlin.collections.ArrayList


class ReportWeekGoalAdapter(internal var context: Context) :
    RecyclerView.Adapter<ReportWeekGoalAdapter.MyViewHolder>(){

    private val data = Utils.getCurrentWeekByFirstDay(context)
    val dbHelper = DataHelper(context)
    internal var mEventListener: EventListener? = null
    var completedCount = 0

    fun getItem(pos: Int): String {
        return data[pos]
    }

    fun addAll(mData: ArrayList<String>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: String) {

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemReportWeekDayBinding>(
            inflater,
            R.layout.item_report_week_day, parent, false)
        rowSideMenuBinding.root.layoutParams.width = parent.measuredWidth / 7
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = getItem(position)
        val date = Utils.parseTime(item, Constant.CapDateFormatDisplay)
        val currDate = Utils.parseTime(Date(), Constant.CapDateFormatDisplay)
        holder.rowSideMenuBinding.tvDate.text =
            Utils.parseTime(item, Constant.CapDateFormatDisplay, "d")
        holder.rowSideMenuBinding.tvDay.text =
            Utils.parseTime(item, Constant.CapDateFormatDisplay, "E")[0].toString()

        if (date == currDate) {
            holder.rowSideMenuBinding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.primary))
        } else {
            holder.rowSideMenuBinding.tvDate.setTextColor(ContextCompat.getColor(context,R.color.col_999))
        }

        when {
            dbHelper.isHistoryAvailable(Utils.parseTime(item, Constant.CapDateFormatDisplay,Constant.DATE_FORMAT)) -> {
                holder.rowSideMenuBinding.imgCompleted.setImageResource(R.drawable.ic_challenge_complete_day)
                completedCount += 1
            }
            else -> {
                holder.rowSideMenuBinding.imgCompleted.setImageResource(R.drawable.bg_circle_border)
            }
        }

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

    }


    override fun getItemCount(): Int {
        return 7
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemReportWeekDayBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
