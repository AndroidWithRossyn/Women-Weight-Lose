package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.R
import com.loseweight.ChooseYourPlanActivity
import com.loseweight.databinding.ItemBodyFocusBinding
import com.loseweight.databinding.ItemRandomWorkoutBinding
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils
import kotlin.collections.ArrayList


class RandomWorkoutAdapter(internal var context: Context) : RecyclerView.Adapter<RandomWorkoutAdapter.MyViewHolder>(){

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemRandomWorkoutBinding>(
            inflater,
            R.layout.item_random_workout, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        holder.rowSideMenuBinding.tvPlanName.text = item.planName
        holder.rowSideMenuBinding.tvPlanText.text = item.planText
        holder.rowSideMenuBinding.imgPlan.setImageResource(Utils.getDrawableId(item.planImage,context))

        holder.rowSideMenuBinding.mContainer.setOnClickListener {
            if(mEventListener !=null)
            {
                mEventListener!!.onItemClick(position,it)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemRandomWorkoutBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
