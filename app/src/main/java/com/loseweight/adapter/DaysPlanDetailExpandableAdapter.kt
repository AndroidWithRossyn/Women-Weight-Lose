package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bignerdranch.expandablerecyclerview.ChildViewHolder
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter
import com.bignerdranch.expandablerecyclerview.ParentViewHolder
import com.bignerdranch.expandablerecyclerview.model.Parent
import com.loseweight.R
import com.loseweight.databinding.ItemDaysChildBinding
import com.loseweight.databinding.ItemDaysParentBinding
import com.loseweight.databinding.ItemHistoryChildBinding
import com.loseweight.databinding.ItemHistoryParentBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.loseweight.objects.HistoryDetailsClass
import com.loseweight.objects.HistoryWeekDataClass
import com.loseweight.objects.PWeekDayData
import com.loseweight.objects.PWeeklyDayData
import com.utillity.db.DataHelper
import java.lang.Exception
import java.util.*
import kotlin.Comparator
import kotlin.math.roundToInt


class DaysPlanDetailExpandableAdapter :
    ExpandableRecyclerAdapter<PWeeklyDayData, PWeekDayData, DaysPlanDetailExpandableAdapter.GrpViewHolder, DaysPlanDetailExpandableAdapter.ItemViewHolder> {

    private var mInflater: LayoutInflater? = null
    private var context: Context? = null
    private var mEventListener: EventListener? = null
    private var isMultiSelect = false
    private var boolFlagWeekComplete = false
    private lateinit var dbHelper: DataHelper


    constructor(
        context: Context?,
        groups: List<PWeeklyDayData?>
    ) : super(groups) {
        this.context = context
        getParentList().clear()
        getParentList().addAll(groups)
        dbHelper = DataHelper(context!!)
        mInflater = LayoutInflater.from(context)
    }

    fun getAll(): List<PWeeklyDayData?>? {
        return getParentList()
    }

    fun getMenuItem(position: Int): PWeeklyDayData? {
        return getParentList().get(position)
    }

    fun getMenuSubItem(position: Int, child: Int): PWeekDayData? {
        return getParentList().get(position).getChildList().get(child)
    }

    fun addAll(groups: List<PWeeklyDayData?>) {
        getParentList().clear()
        getParentList().addAll(groups)
        notifyParentDataSetChanged(false)
    }

    fun setMultiSelect(multiSelect: Boolean) {
        isMultiSelect = multiSelect
    }


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onCreateParentViewHolder(
        parentViewGroup: ViewGroup,
        viewType: Int
    ): GrpViewHolder {
        val inflater = LayoutInflater.from(parentViewGroup.context)

        val rowSideMenuBinding = DataBindingUtil.inflate<ItemDaysParentBinding>(
            inflater,
            R.layout.item_days_parent, parentViewGroup, false
        )

        return GrpViewHolder(rowSideMenuBinding)
    }

    override fun onCreateChildViewHolder(childViewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(childViewGroup.context)

        val rowSideMenuBinding = DataBindingUtil.inflate<ItemDaysChildBinding>(
            inflater,
            R.layout.item_days_child, childViewGroup, false
        )

        return ItemViewHolder(rowSideMenuBinding)
    }

    override fun onBindParentViewHolder(
        parentViewHolder: GrpViewHolder, parentPosition: Int,
        recipe: PWeeklyDayData
    ) {
        parentViewHolder.parentBinding.tvWeekName.text =
            "${context!!.resources.getString(R.string.week)} ${recipe.Week_name.replace("0", "")}"
        parentViewHolder.parentBinding.tvCurrentDay.text = "0"
        parentViewHolder.parentBinding.tvCurrentDay.visibility = View.VISIBLE
        parentViewHolder.parentBinding.tvWeekDays.visibility = View.VISIBLE
        parentViewHolder.parentBinding.llWellDone.visibility = View.GONE

        if (!boolFlagWeekComplete && recipe.Is_completed == "0") {
            boolFlagWeekComplete = true

            var count = 0
            for (i in 0 until recipe.arrWeekDayData.size) {
                if (recipe.arrWeekDayData[i].Is_completed == "1") {
                    count++
                }
            }

            parentViewHolder.parentBinding.tvCurrentDay.text = "$count"
        } else if (recipe.Is_completed == "1") {
            parentViewHolder.parentBinding.tvCurrentDay.visibility = View.GONE
            parentViewHolder.parentBinding.tvWeekDays.visibility = View.GONE
            parentViewHolder.parentBinding.llWellDone.visibility = View.VISIBLE
        }

        parentViewHolder.parentBinding.llContainer.setOnClickListener {

        }

    }

    override fun onBindChildViewHolder(
        childHolder: ItemViewHolder,
        parentPosition: Int, childPosition: Int,
        ingredient: PWeekDayData
    ) {
        childHolder.childBinding.tvDay.text =
            "Day " + ingredient.Day_name.toInt() //+ (parentPosition * 7)).toString()

        if (parentList[parentPosition].flagPrevDay && parentPosition != 0) {
            parentList[parentPosition].flagPrevDay =
                parentList[parentPosition - 1].Is_completed == "1"
        }

        if (parentList[parentPosition].Is_completed == "1") {
            childHolder.childBinding.imgCompleted.visibility = View.VISIBLE
            childHolder.childBinding.flProgress.visibility = View.GONE
            childHolder.childBinding.tvStart.visibility = View.GONE

        }

        when {
            ingredient.Is_completed == "1" -> {
                childHolder.childBinding.imgCompleted.visibility = View.VISIBLE
                childHolder.childBinding.flProgress.visibility = View.GONE
                childHolder.childBinding.tvStart.visibility = View.GONE
                childHolder.childBinding.imgRest.visibility = View.GONE
                childHolder.childBinding.container.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                        context!!,
                        R.color.primary_light
                    )
                )
                childHolder.childBinding.tvDay.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.white
                    )
                )

                parentList[parentPosition].flagPrevDay = true
            }
            parentList[parentPosition].flagPrevDay -> {

                //current day
                childHolder.childBinding.imgRest.visibility = View.GONE
                childHolder.childBinding.imgCompleted.visibility = View.GONE
                childHolder.childBinding.tvDay.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.white
                    )
                )
                childHolder.childBinding.container.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                        context!!,
                        R.color.primary
                    )
                )

                val completed = dbHelper.getCompleteDayExList(ingredient.Day_id)
                if (completed > 0) {
                    if (ingredient.Workouts?.toFloat() ?: 0f > 0) {
                        childHolder.childBinding.flProgress.visibility = View.VISIBLE
                        childHolder.childBinding.tvStart.visibility = View.GONE
                        childHolder.childBinding.circularProgressBar.progressMax =
                            ingredient.Workouts?.toFloat() ?: 0f
                        childHolder.childBinding.circularProgressBar.progress =
                            completed.toFloat()
                        childHolder.childBinding.tvPercentage.text =
                            ((completed.toFloat() * 100) / (ingredient.Workouts?.toFloat()
                                ?: 0f)).roundToInt().toString() + "%"
                        childHolder.childBinding.tvPercentage.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.white
                            )
                        )

                        childHolder.childBinding.circularProgressBar.backgroundProgressBarColor =
                            ContextCompat.getColor(context!!, R.color.white_transparent)
                        childHolder.childBinding.circularProgressBar.progressBarColor =
                            ContextCompat.getColor(context!!, R.color.white)
                    } else {
                        childHolder.childBinding.flProgress.visibility = View.GONE
                        childHolder.childBinding.tvStart.visibility = View.GONE
                        childHolder.childBinding.imgRest.visibility = View.VISIBLE
                    }
                } else {
                    if (ingredient.Workouts?.toFloat() ?: 0f > 0) {
                        childHolder.childBinding.flProgress.visibility = View.GONE
                        childHolder.childBinding.tvStart.visibility = View.VISIBLE
                    } else {
                        childHolder.childBinding.flProgress.visibility = View.GONE
                        childHolder.childBinding.tvStart.visibility = View.GONE
                        childHolder.childBinding.imgRest.visibility = View.VISIBLE
                        childHolder.childBinding.imgRest.imageTintList =
                            ContextCompat.getColorStateList(
                                context!!,
                                R.color.white
                            )
                    }
                }

                parentList[parentPosition].flagPrevDay = false
            }
            else -> {
//                            holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_gray)
                childHolder.childBinding.container.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                        context!!,
                        R.color.white
                    )
                )
                val completed = dbHelper.getCompleteDayExList(ingredient.Day_id)


                childHolder.childBinding.flProgress.visibility = View.VISIBLE
                childHolder.childBinding.tvStart.visibility = View.GONE
                childHolder.childBinding.imgCompleted.visibility = View.GONE

                if (ingredient.Workouts?.toFloat() ?: 0f > 0) {
                    childHolder.childBinding.imgRest.visibility = View.GONE
                    childHolder.childBinding.tvPercentage.text =
                        ((completed.toFloat() * 100) / (ingredient.Workouts?.toFloat()
                            ?: 0f)).roundToInt().toString() + "%"

                    childHolder.childBinding.circularProgressBar.progressMax =
                        ingredient.Workouts?.toFloat() ?: 0f
                    childHolder.childBinding.circularProgressBar.progress = completed.toFloat()
                } else {
                    childHolder.childBinding.flProgress.visibility = View.GONE
                    childHolder.childBinding.imgRest.visibility = View.VISIBLE
                }
                childHolder.childBinding.tvDay.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.col_999
                    )
                )
                childHolder.childBinding.tvPercentage.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.col_999
                    )
                )
                childHolder.childBinding.circularProgressBar.backgroundProgressBarColor =
                    ContextCompat.getColor(context!!, R.color.gray_light__)
                childHolder.childBinding.circularProgressBar.progressBarColor =
                    ContextCompat.getColor(context!!, R.color.primary)

                parentList[parentPosition].flagPrevDay = false
            }
        }

        childHolder.childBinding.container.setOnClickListener(View.OnClickListener {
            if (mEventListener != null) {
                mEventListener!!.OnDayClick(parentPosition, childPosition)
            }
        })

    }

    fun getItem(position: Int): PWeeklyDayData? {
        return getParentList().get(position)
    }

    class GrpViewHolder(val parentBinding: ItemDaysParentBinding) :
        ParentViewHolder<Parent<Any?>, Any?>(parentBinding.root) {
    }


    class ItemViewHolder(val childBinding: ItemDaysChildBinding) :
        ChildViewHolder<Any?>(childBinding.root) {
    }


    interface EventListener {
        fun OnDayClick(parentPosition: Int, childPosition: Int)
    }

    fun setEventListener(eventlistener: EventListener?) {
        mEventListener = eventlistener
    }
}