package com.stretching.utils

import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView

class CountDownAnimation {
    private var mTextView: TextView? = null
    private var mAnimation: Animation? = null
    private var mStartCount = 0
    private var mCurrentCount = 0
    private var mListener: CountDownListener? = null

    private val mHandler: Handler = Handler()

    private val mCountDown = Runnable {
        if (mCurrentCount > 0) {
            mTextView!!.setText(mCurrentCount.toString() + "")
            mTextView!!.startAnimation(mAnimation)
            mCurrentCount--
        } else {
            mTextView!!.setVisibility(View.GONE)
            if (mListener != null) mListener!!.onCountDownEnd(this@CountDownAnimation)
        }
    }

    /**
     *
     *
     * Creates a count down animation in the <var>textView</var>, starting from
     * <var>startCount</var>.
     *
     *
     *
     * By default, the class defines a fade out animation, which uses
     * [AlphaAnimation] from 1 to 0.
     *
     *
     * @param textView
     * The view where the count down will be shown
     * @param startCount
     * The starting count number
     */
    constructor(textView: TextView?, startCount: Int) {
        mTextView = textView
        mStartCount = startCount
        mAnimation = AlphaAnimation(1.0f, 0.0f)
        mAnimation!!.setDuration(1000)
    }

    /**
     * Starts the count down animation.
     */
    fun start() {
        mHandler.removeCallbacks(mCountDown)
        mTextView!!.setText(mStartCount.toString() + "")
        mTextView!!.setVisibility(View.VISIBLE)
        mCurrentCount = mStartCount
        mHandler.post(mCountDown)
        for (i in 1..mStartCount) {
            mHandler.postDelayed(mCountDown, i * 1000L)
        }
    }

    /**
     * Cancels the count down animation.
     */
    fun cancel() {
        mHandler.removeCallbacks(mCountDown)
        mTextView!!.setText("")
        mTextView!!.setVisibility(View.GONE)
    }

    /**
     * Sets the animation used during the count down. If the duration of the
     * animation for each number is not set, one second will be defined.
     */
    fun setAnimation(animation: Animation?) {
        mAnimation = animation
        if (mAnimation!!.getDuration() === 0L) mAnimation!!.setDuration(1000)
    }

    /**
     * Returns the animation used during the count down.
     */
    fun getAnimation(): Animation? {
        return mAnimation
    }

    /**
     * Sets a new starting count number for the count down animation.
     *
     * @param startCount
     * The starting count number
     */
    fun setStartCount(startCount: Int) {
        mStartCount = startCount
    }

    /**
     * Returns the starting count number for the count down animation.
     */
    fun getStartCount(): Int {
        return mStartCount
    }

    /**
     * Binds a listener to this count down animation. The count down listener is
     * notified of events such as the end of the animation.
     *
     * @param listener
     * The count down listener to be notified
     */
    fun setCountDownListener(listener: CountDownListener?) {
        mListener = listener
    }

    /**
     * A count down listener receives notifications from a count down animation.
     * Notifications indicate count down animation related events, such as the
     * end of the animation.
     */
    interface CountDownListener {
        /**
         * Notifies the end of the count down animation.
         *
         * @param animation
         * The count down animation which reached its end.
         */
        fun onCountDownEnd(animation: CountDownAnimation?)
    }
}