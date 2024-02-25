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
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils
import kotlin.collections.ArrayList


class ChooseYourPlanAdapter(internal var context: Context) :
    RecyclerView.Adapter<ChooseYourPlanAdapter.MyViewHolder>(){

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

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemChooseYourPlanBinding>(
            inflater,
            R.layout.item_choose_your_plan, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        holder.rowSideMenuBinding.tvTitle.text = item.planName
        holder.rowSideMenuBinding.ivIcon.setImageResource(Utils.getDrawableId("ic_goal_"+item.planImage,context))

        if (item.isSelected) {
            holder.rowSideMenuBinding.ivCheck.visibility = View.VISIBLE
            holder.rowSideMenuBinding.container.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.primary))
            holder.rowSideMenuBinding.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.rowSideMenuBinding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
        }
        else {
            holder.rowSideMenuBinding.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.col_343), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.rowSideMenuBinding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.col_343))
            holder.rowSideMenuBinding.ivCheck.visibility = View.GONE
            holder.rowSideMenuBinding.container.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.gray_light__))
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

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemChooseYourPlanBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
