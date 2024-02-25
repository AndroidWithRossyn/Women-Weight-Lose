package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loseweight.R
import com.loseweight.databinding.ItemWorkoutStatusIndicatorBinding
import java.lang.Exception
import kotlin.collections.ArrayList


class WorkoutProgressIndicatorAdapter(internal var context: Context) :
    RecyclerView.Adapter<WorkoutProgressIndicatorAdapter.MyViewHolder>() {

    var totalEx = 10
    var completedEx = 0
    internal var mEventListener: EventListener? = null

    fun setTotalExercise(totalEx: Int) {
        this.totalEx = totalEx
        notifyDataSetChanged()
    }

    fun setCompletedExercise(count: Int) {
        completedEx = count
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemWorkoutStatusIndicatorBinding>(
            inflater,
            R.layout.item_workout_status_indicator, parent, false
        )
        rowSideMenuBinding.root.layoutParams.width = parent.measuredWidth / itemCount
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            if (position <= (completedEx - 1)) {
                holder.rowSideMenuBinding.viewIndicator.background =
                    ContextCompat.getDrawable(context, R.color.primary)
            } else {
                holder.rowSideMenuBinding.viewIndicator.background =
                    ContextCompat.getDrawable(context, R.color.gray_light_)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.rowSideMenuBinding.viewIndicator.background =
                ContextCompat.getDrawable(context, R.color.gray_light_)
        }


    }


    override fun getItemCount(): Int {
        return totalEx
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemWorkoutStatusIndicatorBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
