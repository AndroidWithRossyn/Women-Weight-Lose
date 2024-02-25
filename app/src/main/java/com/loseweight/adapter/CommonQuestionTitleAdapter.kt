package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.R
import com.loseweight.databinding.ItemCommonQuestionTitleBinding
import com.loseweight.utils.Utils
import com.stretching.objects.CommonQuestionClass
import com.stretching.objects.CommonQuestionDataClass


class CommonQuestionTitleAdapter(internal var context: Context) :
    RecyclerView.Adapter<CommonQuestionTitleAdapter.MyViewHolder>() {

    private val data = mutableListOf<CommonQuestionDataClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): CommonQuestionDataClass {
        return data[pos]
    }

    fun addAll(mData: ArrayList<CommonQuestionDataClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: CommonQuestionDataClass) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun removeAt(pos: Int) {
        data.removeAt(pos)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemCommonQuestionTitleBinding>(
            inflater,
            R.layout.item_common_question_title, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        if (item.isSelected) {
            holder.rowSideMenuBinding.tvTitle.background = ContextCompat.getDrawable(context, R.drawable.btn_bg_round_theme)
            holder.rowSideMenuBinding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
        } else {
            holder.rowSideMenuBinding.tvTitle.background = ContextCompat.getDrawable(context, R.drawable.btn_bg_round_border_gray)
            holder.rowSideMenuBinding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.gray_light_))
        }

        holder.rowSideMenuBinding.tvTitle.text = item.title
        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelection(pos: Int) {
        for (i in data.indices) {
            data[i].isSelected = i == pos
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemCommonQuestionTitleBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
