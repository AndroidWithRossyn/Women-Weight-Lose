package com.common.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class SimpleListDividerDecorator
/**
 * Constructor.
 *
 * @param horizontalDivider horizontal divider drawable
 * @param verticalDivider   vertical divider drawable
 * @param overlap           whether the divider is drawn overlapped on bottom (or right) of the item.
 */
(private val mHorizontalDrawable: Drawable?, private val mVerticalDrawable: Drawable?, private val mOverlap: Boolean) : RecyclerView.ItemDecoration() {
    private val mHorizontalDividerHeight: Int
    private val mVerticalDividerWidth: Int

    /**
     * Constructor.
     *
     * @param divider horizontal divider drawable
     * @param overlap whether the divider is drawn overlapped on bottom of the item.
     */
    constructor(divider: Drawable?, overlap: Boolean) : this(divider, null, overlap) {}

    init {
        mHorizontalDividerHeight = mHorizontalDrawable?.intrinsicHeight ?: 0
        mVerticalDividerWidth = mVerticalDrawable?.intrinsicWidth ?: 0
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount


        if (childCount == 0) {
            return
        }

        val xPositionThreshold = if (mOverlap) 1.0f else mVerticalDividerWidth + 1.0f // [px]
        val yPositionThreshold = if (mOverlap) 1.0f else mHorizontalDividerHeight + 1.0f // [px]
        val zPositionThreshold = 1.0f // [px]

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val nextChild = parent.getChildAt(i + 1)

            if (child.visibility != View.VISIBLE || nextChild.visibility != View.VISIBLE) {
                continue
            }

            // check if the next item is placed at the bottom or right
            val childBottom = child.bottom + ViewCompat.getTranslationY(child)
            val nextChildTop = nextChild.top + ViewCompat.getTranslationY(nextChild)
            val childRight = child.right + ViewCompat.getTranslationX(child)
            val nextChildLeft = nextChild.left + ViewCompat.getTranslationX(nextChild)

            if (!(mHorizontalDividerHeight != 0 && Math.abs(nextChildTop - childBottom) < yPositionThreshold || mVerticalDividerWidth != 0 && Math.abs(nextChildLeft - childRight) < xPositionThreshold)) {
                continue
            }

            // check if the next item is placed on the same plane
            val childZ = ViewCompat.getTranslationZ(child) + ViewCompat.getElevation(child)
            val nextChildZ = ViewCompat.getTranslationZ(nextChild) + ViewCompat.getElevation(nextChild)

            if (Math.abs(nextChildZ - childZ) >= zPositionThreshold) {
                continue
            }

            val childAlpha = ViewCompat.getAlpha(child)
            val nextChildAlpha = ViewCompat.getAlpha(nextChild)

            val tx = (ViewCompat.getTranslationX(child) + 0.5f).toInt()
            val ty = (ViewCompat.getTranslationY(child) + 0.5f).toInt()

            if (mHorizontalDividerHeight != 0) {
                val left = child.left
                val right = child.right
                val top = child.bottom - if (mOverlap) mHorizontalDividerHeight else 0
                val bottom = top + mHorizontalDividerHeight

                mHorizontalDrawable!!.alpha = (0.5f * 255 * (childAlpha + nextChildAlpha) + 0.5f).toInt()
                mHorizontalDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty)
                mHorizontalDrawable.draw(c)
            }

            if (mVerticalDividerWidth != 0) {
                val left = child.right - if (mOverlap) mVerticalDividerWidth else 0
                val right = left + mVerticalDividerWidth
                val top = child.top
                val bottom = child.bottom

                mVerticalDrawable!!.alpha = (0.5f * 255 * (childAlpha + nextChildAlpha) + 0.5f).toInt()
                mVerticalDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty)
                mVerticalDrawable.draw(c)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (mOverlap) {
            outRect.set(0, 0, 0, 0)
        } else {
            outRect.set(0, 0, mVerticalDividerWidth, mHorizontalDividerHeight)
        }
    }
}