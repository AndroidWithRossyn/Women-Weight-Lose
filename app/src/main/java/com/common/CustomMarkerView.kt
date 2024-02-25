package com.common

import android.content.Context
import android.view.View
import android.widget.TextView
import com.charting.components.MarkerView
import com.charting.data.Entry
import com.charting.highlight.Highlight
import com.charting.utils.MPPointF
import com.loseweight.R


class CustomMarkerView(context: Context?, layoutResource: Int) : MarkerView(context,layoutResource) {
    private var tvContent: TextView? = null
    init {
        tvContent = findViewById<View>(R.id.tvMarker) as TextView
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        tvContent!!.text = "" + e.y

        // this will perform necessary layouting
        super.refreshContent(e, highlight)
    }

    private var mOffset: MPPointF? = null
    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat()-20)
        }
        return mOffset!!
    }
}