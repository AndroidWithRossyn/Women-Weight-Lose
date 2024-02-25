package com.loseweight.utils

import android.os.Handler



internal object ExitStrategy {

    private var isAbletoExit = false
    private val h = Handler()

    fun canExit(): Boolean {
        return isAbletoExit
    }

    fun startExitDelay(delayMillis: Long) {
        isAbletoExit = true
        h.postDelayed(runnable, delayMillis)
    }

    internal var runnable: Runnable = Runnable { isAbletoExit = false }

    fun shutDown() {
        isAbletoExit = false
        h.removeCallbacks(runnable)
    }

}