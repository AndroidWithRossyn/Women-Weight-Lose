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
import com.loseweight.databinding.ItemEditPlanBinding
import com.loseweight.databinding.ItemReplaceWorkoutListBinding
import com.loseweight.objects.ExTableClass
import com.loseweight.objects.HomeExTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class ReplaceExerciseAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data = arrayListOf<ExTableClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): ExTableClass {
        return data[pos]
    }

    fun getPos(item :ExTableClass){
        data.indexOf(item)
    }

    fun addAll(mData: ArrayList<ExTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: ExTableClass) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun removeAt(position: Int){
        try {
            this.data.removeAt(position)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun onChangePosition(fromPos: Int, toPos: Int) {
        //Collections.swap(data, fromPos, toPos)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemReplaceWorkoutListBinding>(
            inflater,
            R.layout.item_replace_workout_list, parent, false
        )

        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val item = getItem(position)

        val holder = viewHolder as MyViewHolder

        holder.rowSideMenuBinding.tvName.text = item.exName

        holder.rowSideMenuBinding.rbSelected.visibility = View.VISIBLE

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(item, holder.rowSideMenuBinding.root)
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

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemReplaceWorkoutListBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(item: ExTableClass, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
