// Copyright 2012 Square, Inc.
package com.common.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.loseweight.R


/**
 * Maintains an aspect ratio based on either width or height. Disabled by default.
 */
class AspectRatioImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {

    private var aspectRatio: Float = 0.toFloat()
    private var aspectRatioEnabled: Boolean = false
    private var dominantMeasurement: Int = 0

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
        aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspectRatio, DEFAULT_ASPECT_RATIO)
        aspectRatioEnabled = a.getBoolean(
            R.styleable.AspectRatioImageView_aspectRatioEnabled,
            DEFAULT_ASPECT_RATIO_ENABLED
        )
        dominantMeasurement = a.getInt(
            R.styleable.AspectRatioImageView_dominantMeasurement,
            DEFAULT_DOMINANT_MEASUREMENT
        )
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!aspectRatioEnabled) return

        val newWidth: Int
        val newHeight: Int
        when (dominantMeasurement) {
            MEASUREMENT_WIDTH -> {
                newWidth = getMeasuredWidth()
                newHeight = (newWidth * aspectRatio).toInt()
            }

            MEASUREMENT_HEIGHT -> {
                newHeight = getMeasuredHeight()
                newWidth = (newHeight * aspectRatio).toInt()
            }

            else -> throw IllegalStateException("Unknown measurement with ID $dominantMeasurement")
        }

        setMeasuredDimension(newWidth, newHeight)
    }

    /**
     * Get the aspect ratio for this image view.
     */
    fun getAspectRatio(): Float {
        return aspectRatio
    }

    /**
     * Set the aspect ratio for this image view. This will update the view instantly.
     */
    fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
        if (aspectRatioEnabled) {
            requestLayout()
        }
    }

    /**
     * Get whether or not forcing the aspect ratio is enabled.
     */
    fun getAspectRatioEnabled(): Boolean {
        return aspectRatioEnabled
    }

    /**
     * set whether or not forcing the aspect ratio is enabled. This will re-layout the view.
     */
    fun setAspectRatioEnabled(aspectRatioEnabled: Boolean) {
        this.aspectRatioEnabled = aspectRatioEnabled
        requestLayout()
    }

    /**
     * Get the dominant measurement for the aspect ratio.
     */
    fun getDominantMeasurement(): Int {
        return dominantMeasurement
    }

    /**
     * Set the dominant measurement for the aspect ratio.
     *
     * @see .MEASUREMENT_WIDTH
     *
     * @see .MEASUREMENT_HEIGHT
     */
    fun setDominantMeasurement(dominantMeasurement: Int) {
        if (dominantMeasurement != MEASUREMENT_HEIGHT && dominantMeasurement != MEASUREMENT_WIDTH) {
            throw IllegalArgumentException("Invalid measurement type.")
        }
        this.dominantMeasurement = dominantMeasurement
        requestLayout()
    }

    companion object {
        // NOTE: These must be kept in sync with the AspectRatioImageView attributes in attrs.xml.
        val MEASUREMENT_WIDTH = 0
        val MEASUREMENT_HEIGHT = 1

        private val DEFAULT_ASPECT_RATIO = 1f
        private val DEFAULT_ASPECT_RATIO_ENABLED = false
        private val DEFAULT_DOMINANT_MEASUREMENT = MEASUREMENT_WIDTH
    }
}