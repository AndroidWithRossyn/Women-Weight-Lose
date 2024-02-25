package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bignerdranch.expandablerecyclerview.ChildViewHolder
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter
import com.bignerdranch.expandablerecyclerview.ParentViewHolder
import com.bignerdranch.expandablerecyclerview.model.Parent
import com.loseweight.R
import com.loseweight.databinding.ItemCommonQuestionChildBinding
import com.loseweight.databinding.ItemCommonQuestionParentBinding
import com.stretching.objects.CommonQuestionClass
import com.stretching.objects.CommonQuestionDataClass


class CommonQuestionExpandableAdapter : ExpandableRecyclerAdapter<CommonQuestionDataClass, CommonQuestionClass, CommonQuestionExpandableAdapter.GrpViewHolder, CommonQuestionExpandableAdapter.ItemViewHolder> {

    private var mInflater: LayoutInflater? = null
    private var context: Context? = null
    private var mEventListener: EventListener? = null
    private var isMultiSelect = false


    constructor(
        context: Context?,
        groups: List<CommonQuestionDataClass?>
    ) : super(groups) {
        this.context = context
        getParentList().clear()
        getParentList().addAll(groups)
        mInflater = LayoutInflater.from(context)
    }

    fun getAll(): List<CommonQuestionDataClass?>? {
        return getParentList()
    }

    fun getMenuItem(position: Int): CommonQuestionDataClass? {
        return getParentList().get(position)
    }

    fun getMenuSubItem(position: Int, child: Int): CommonQuestionClass? {
        return getParentList().get(position).getChildList().get(child)
    }

    fun addAll(groups: List<CommonQuestionDataClass?>) {
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

        val rowSideMenuBinding = DataBindingUtil.inflate<ItemCommonQuestionParentBinding>(
            inflater,
            R.layout.item_common_question_parent, parentViewGroup, false
        )

        return GrpViewHolder(rowSideMenuBinding)
    }

    override fun onCreateChildViewHolder(childViewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(childViewGroup.context)

        val rowSideMenuBinding = DataBindingUtil.inflate<ItemCommonQuestionChildBinding>(
            inflater,
            R.layout.item_common_question_child, childViewGroup, false
        )

        return ItemViewHolder(rowSideMenuBinding)
    }

    override fun onBindParentViewHolder(
        parentViewHolder: GrpViewHolder, parentPosition: Int,
        recipe: CommonQuestionDataClass
    ) {

       parentViewHolder.parentBinding.llContainer.setOnClickListener {

       }
       parentViewHolder.parentBinding.tvTitle.text = "About "+recipe.title

    }

    override fun onBindChildViewHolder(
        childHolder: ItemViewHolder,
        parentPosition: Int, childPosition: Int,
        ingredient: CommonQuestionClass
    ) {

        childHolder.childBinding.tvQuestion.text = ingredient.question
        childHolder.childBinding.tvAns.text = ingredient.answer

        if(parentPosition == parentList.lastIndex && childPosition == parentList[parentPosition].childList.lastIndex)
            childHolder.childBinding.spaceView.visibility = View.VISIBLE
        else
            childHolder.childBinding.spaceView.visibility = View.GONE

        childHolder.childBinding.tvQuestion.setOnClickListener(View.OnClickListener {
            if(childHolder.childBinding.tvAns.visibility == View.VISIBLE)
            {
                childHolder.childBinding.tvAns.visibility = View.GONE
                childHolder.childBinding.imgArrowUp.animate().rotation(0f).start()
            }else{
                childHolder.childBinding.tvAns.visibility = View.VISIBLE
                childHolder.childBinding.imgArrowUp.animate().rotation(180f).start()
            }
        })

        childHolder.childBinding.imgArrowUp.setOnClickListener(View.OnClickListener {
            if(childHolder.childBinding.tvAns.visibility == View.VISIBLE)
            {
                childHolder.childBinding.tvAns.visibility = View.GONE
                childHolder.childBinding.imgArrowUp.animate().rotation(0f).start()
            }else{
                childHolder.childBinding.tvAns.visibility = View.VISIBLE
                childHolder.childBinding.imgArrowUp.animate().rotation(180f).start()
            }
        })

    }

    fun getItem(position: Int): CommonQuestionDataClass? {
        return getParentList().get(position)
    }

    class GrpViewHolder(val parentBinding: ItemCommonQuestionParentBinding) :
        ParentViewHolder<Parent<Any?>, Any?>(parentBinding.root) {
    }


    class ItemViewHolder(val childBinding: ItemCommonQuestionChildBinding) :
        ChildViewHolder<Any?>(childBinding.root) {
    }


    interface EventListener {
        fun OnMenuClick(parentPosition: Int, childPosition: Int)
        fun OnMoreClick(parentPosition: Int, childPosition: Int,view:View)
    }

    fun setEventListener(eventlistener: EventListener?) {
        mEventListener = eventlistener
    }
}