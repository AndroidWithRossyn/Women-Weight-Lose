package com.loseweight.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@SuppressLint("StaticFieldLeak")
internal object EndlessList {
    private var isLock = false
    private var enable = true
    private var stackFromEnd = false
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0

    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var recyclerView: RecyclerView? = null

    fun EndlessList(recyclerView: RecyclerView, linearLayoutManager: LinearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager
        EndlessList.recyclerView = recyclerView
        EndlessList.recyclerView!!.setOnScrollListener(onScrollListener)
    }

    fun setStackFromEnd(stackFromEnd: Boolean) {
        EndlessList.stackFromEnd = stackFromEnd
    }

    internal var onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (enable) {
                if (stackFromEnd) {
                    firstVisibleItem = mLinearLayoutManager!!.findFirstVisibleItemPosition()
                    val loadMore = firstVisibleItem == 0
                    if (loadMore && !isLock) {
                        if (loadMoreListener != null) {
                            loadMoreListener!!.onLoadMore()
                        }
                    }
                } else {
                    visibleItemCount = recyclerView.childCount
                    totalItemCount = mLinearLayoutManager!!.itemCount
                    firstVisibleItem = mLinearLayoutManager!!.findFirstVisibleItemPosition()
                    val loadMore = firstVisibleItem + visibleItemCount >= totalItemCount
                    if (loadMore && !isLock) {
                        if (loadMoreListener != null) {
                            loadMoreListener!!.onLoadMore()
                        }
                    }
                }
            }
        }
    }

    fun lockMoreLoading() {
        isLock = true
    }

    fun releaseLock() {
        isLock = false
    }

    fun disableLoadMore() {
        enable = false
    }

    fun setOnLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        EndlessList.loadMoreListener = loadMoreListener
    }

    internal var loadMoreListener: OnLoadMoreListener? = null

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}