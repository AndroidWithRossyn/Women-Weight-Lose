package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loseweight.R
import com.loseweight.databinding.ItemWorkoutListBinding
import com.loseweight.objects.HomeExTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import kotlin.collections.ArrayList


class WorkoutListAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data = mutableListOf<HomeExTableClass>()
    internal var mEventListener: EventListener? = null
    var continuePos:Int?= null

    fun getItem(pos: Int): HomeExTableClass {
        return data[pos]
    }

    fun addAll(mData: ArrayList<HomeExTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: HomeExTableClass) {

        try {
            this.data.add(item)
        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun isAnyCompleted():Boolean{
        for (item in data)
        {
            if(item.isCompleted.equals("1"))
            {
                return true
            }
        }
        return false
    }

    fun isAllCompleted():Boolean{
        for (item in data)
        {
            if(item.isCompleted.equals("1").not())
            {
                return false
            }
        }
        return true
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemWorkoutListBinding>(
            inflater,
            R.layout.item_workout_list, parent, false
        )

        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val holder = viewHolder as MyViewHolder
        val item = getItem(position)

        holder.rowSideMenuBinding.tvName.text = item.exName

         if (item.isCompleted.equals("1")) {
             holder.rowSideMenuBinding.imgCompleted.visibility =  View.VISIBLE
        } else {
             holder.rowSideMenuBinding.imgCompleted.visibility = View.GONE
             if(continuePos == null)
             {
                 continuePos = position
             }
        }

        if (item.exUnit.equals(Constant.workout_type_step)) {
            holder.rowSideMenuBinding.tvTime.text = "X ${item.exTime}"
        } else {
            holder.rowSideMenuBinding.tvTime.text =
                Utils.secToString(item.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(item.exPath!!)?.let { Utils.getAssetItems(context, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(context)
                //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(context).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                holder.rowSideMenuBinding.viewFlipper.addView(imgview)
            }
        }

        holder.rowSideMenuBinding.viewFlipper.isAutoStart = true
        holder.rowSideMenuBinding.viewFlipper.setFlipInterval(context.resources.getInteger(R.integer.viewfliper_animation))
        holder.rowSideMenuBinding.viewFlipper.startFlipping()

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemWorkoutListBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
