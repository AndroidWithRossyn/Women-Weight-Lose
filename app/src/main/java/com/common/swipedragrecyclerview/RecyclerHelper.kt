package com.common.swipedragrecyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RecyclerHelper<T>(var list: ArrayList<T>, var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : ItemTouchHelper.Callback() {

    var onDragListener: OnDragListener? = null
    var onSwipeListener: OnSwipeListener? = null
    private var isItemDragEnabled: Boolean = false
    private var isItemSwipeEnbled: Boolean = false

    fun onMoved(fromPos: Int, toPos: Int) {
        list.removeAt(toPos)
        mAdapter.notifyItemRemoved(toPos)
    }

    /*fun addItem(item: MyTrainingCatExTableClass){
        list.add(item as T)
    }

    fun removeItem(pos:Int)
    {
        list.removeAt(pos)
    }

    fun updateItem(pos: Int, exData: MyTrainingCatExTableClass)
    {
        (list!![pos] as MyTrainingCatExTableClass).exReplaceTime = exData.exReplaceTime
    }*/

    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(list, fromPosition, toPosition)
        mAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags: Int = 0;
        var swipeFlags: Int = 0;
        if (isItemDragEnabled) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN;
        }
        if (isItemSwipeEnbled) {
            swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        onItemMoved(viewHolder!!.adapterPosition, target!!.adapterPosition)
        viewHolder?.adapterPosition?.let { target?.adapterPosition?.let { it1 ->
            onDragListener?.onDragItemListener(it,
                it1
            )
        } }
        return true;
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onMoved(viewHolder!!.oldPosition, viewHolder.adapterPosition)
        onSwipeListener?.onSwipeItemListener()
    }

    override fun isLongPressDragEnabled(): Boolean {
        return isItemDragEnabled
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return isItemSwipeEnbled
    }

    fun setRecyclerItemDragEnabled(isDragEnabled: Boolean): RecyclerHelper<T> {
        this.isItemDragEnabled = isDragEnabled
        return this;
    }

    fun setRecyclerItemSwipeEnabled(isSwipeEnabled: Boolean): RecyclerHelper<T> {
        this.isItemSwipeEnbled = isSwipeEnabled
        return this;
    }

    fun setOnDragItemListener(onDragListener: OnDragListener): RecyclerHelper<T> {
        this.onDragListener = onDragListener
        return this;
    }

    fun setOnSwipeItemListener(onSwipeListener: OnSwipeListener): RecyclerHelper<T> {
        this.onSwipeListener = onSwipeListener
        return this;
    }

    fun getData():ArrayList<T>{
        return this.list
    }

}