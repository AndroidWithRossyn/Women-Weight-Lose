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
import com.loseweight.objects.HomeExTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class EditPlanAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data = arrayListOf<HomeExTableClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): HomeExTableClass {
        return data[pos]
    }

    fun getPos(item :HomeExTableClass){
        data.indexOf(item)
    }

    fun addAll(mData: ArrayList<HomeExTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun replace(old:HomeExTableClass,new:HomeExTableClass){
        try {
        new.replaceExId = new.exId
        new.exId = old.exId
        new.dayExId = old.dayExId

        val index = data.indexOf(old)
        data.removeAt(index)
        data.add(index,new)
        } catch (e: Exception) {
            e.printStackTrace()
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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemEditPlanBinding>(
            inflater,
            R.layout.item_edit_plan, parent, false
        )

        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val item = getItem(position)

        val holder = viewHolder as MyViewHolder

        holder.rowSideMenuBinding.tvReplace.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onReplaceClick(item, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.tvName.text = item.exName

        if (item.replaceExId.isNullOrEmpty().not()) {
            holder.rowSideMenuBinding.imgReplaceMark.visibility = View.VISIBLE
        } else {
            holder.rowSideMenuBinding.imgReplaceMark.visibility = View.GONE
        }

        if (item.exUnit.equals(Constant.workout_type_step)) {
            holder.rowSideMenuBinding.tvTime.text = "X ${item.exTime}"
        } else {
            holder.rowSideMenuBinding.tvTime.text =
                Utils.secToString(item.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(item, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.llDelete.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onDeleteClick(item, holder.rowSideMenuBinding.root)
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

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemEditPlanBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(item: HomeExTableClass, view: View)
        fun onReplaceClick(item: HomeExTableClass, view: View)
        fun onDeleteClick(item: HomeExTableClass, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
