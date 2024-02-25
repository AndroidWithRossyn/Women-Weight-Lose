package com.loseweight.utils

import android.text.TextUtils
import android.util.Log



internal object Debug {

    val DEBUG = true
    val DEBUG_IS_HIDE_AD = false
    val DEBUG_IS_PURCHASE = false

    fun e(tag: String, msg: String?) {
        var tag = tag
        if (DEBUG) {
            if (TextUtils.isEmpty(tag))
                tag = "unknown"

            //            Log.e(tag, msg);
            val finalTag = tag
            log(msg, object : LogCB {
                override fun log(message: String) {
                    Log.e(finalTag, message)
                }
            })
        }
    }

    fun i(tag: String, msg: String) {
        var tag = tag
        if (DEBUG) {
            if (TextUtils.isEmpty(tag))
                tag = "unknown"

            val finalTag = tag
            log(msg, object : LogCB {
                override fun log(message: String) {
                    Log.i(finalTag, message)
                }
            })
        }

    }

    fun w(tag: String, msg: String) {
        var tag = tag

        if (DEBUG) {
            if (TextUtils.isEmpty(tag))
                tag = "unknown"
            //            Log.w(tag, msg);
            val finalTag = tag
            log(msg, object : LogCB {
                override fun log(message: String) {
                    Log.w(finalTag, message)
                }
            })
        }
    }

    fun d(tag: String, msg: String) {
        var tag = tag
        if (DEBUG) {
            if (TextUtils.isEmpty(tag))
                tag = "unknown"
            //            Log.d(tag, msg);
            val finalTag = tag
            log(msg, object : LogCB {
                override fun log(message: String) {
                    Log.d(finalTag, message)
                }
            })
        }
    }

    fun v(tag: String, msg: String) {
        var tag = tag
        if (DEBUG) {
            if (TextUtils.isEmpty(tag))
                tag = "unknown"
            //            Log.v(tag, msg);
            val finalTag = tag
            log(msg, object : LogCB {
                override fun log(message: String) {
                    Log.v(finalTag, message)
                }
            })
        }
    }

    private val MAX_LOG_LENGTH = 4000

    private fun log(message: String?, callback: LogCB) {
        if (message == null) {
            callback.log("null")
            return
        }
        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = Math.min(newline, i + MAX_LOG_LENGTH)
                callback.log(message.substring(i, end))
                i = end
            } while (i < newline)
            i++
        }
    }

    private fun log(message: String?, throwable: Throwable?, callback: LogCB) {
        if (throwable == null) {
            log(message, callback)
            return
        }
        if (message != null) {
            log(
                message + "\n" + Log.getStackTraceString(throwable),
                callback
            )
        } else {
            log(Log.getStackTraceString(throwable), callback)
        }
    }

    private interface LogCB {
        fun log(message: String)
    }

}