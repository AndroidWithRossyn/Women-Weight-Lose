package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.R
import com.loseweight.databinding.ItemReminderBinding
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.Utils


class ReminderAdapter(internal var context: Context) :
    RecyclerView.Adapter<ReminderAdapter.MyViewHolder>() {

    private val data = mutableListOf<ReminderTableClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): ReminderTableClass {
        return data[pos]
    }

    fun addAll(mData: java.util.ArrayList<ReminderTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: ReminderTableClass) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun removeAt(pos:Int)
    {
        data.removeAt(pos)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemReminderBinding>(
            inflater,
            R.layout.item_reminder, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        holder.rowSideMenuBinding.tvTime.text = item.remindTime
        holder.rowSideMenuBinding.switchReminder.isChecked = item.isActive.equals("true")


        val strDays = item.days.split(",").sorted()
        holder.rowSideMenuBinding.tvDays.text = ""

        for (i in 0 until strDays.size) {
            if (holder.rowSideMenuBinding.tvDays.text.toString().isEmpty()) {
                holder.rowSideMenuBinding.tvDays.text =
                    Utils.getShortDayName(strDays[i])
            } else {
                holder.rowSideMenuBinding.tvDays.append(
                    (", ").plus(
                        Utils.getShortDayName(strDays[i])
                    )
                )
            }
        }

        holder.rowSideMenuBinding.llRepeat.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onRepeatClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.tvTime.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onTimeClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.imgDelete.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onDeleteClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.switchReminder.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (mEventListener != null) {
                    mEventListener!!.onSwitchChecked(
                        position,
                        isChecked,
                        holder.rowSideMenuBinding.root
                    )
                }
            }
        })

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemReminderBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onRepeatClick(position: Int, view: View)
        fun onTimeClick(position: Int, view: View)
        fun onDeleteClick(position: Int, view: View)
        fun onSwitchChecked(position: Int, isChecked: Boolean, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
