package com.common.view

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter

import androidx.viewpager.widget.ViewPager


class CustomViewPager : ViewPager {
    var mPagerAdapter: PagerAdapter? = null
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mPagerAdapter != null) {
            super.setAdapter(mPagerAdapter)
        }
    }

    override fun setAdapter(adapter: PagerAdapter?) {}
    fun storeAdapter(pagerAdapter: PagerAdapter?) {
        mPagerAdapter = pagerAdapter
    }

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
}