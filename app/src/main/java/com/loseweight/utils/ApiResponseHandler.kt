package com.loseweight.utils

import okhttp3.Callback


abstract class ApiResponseHandler : Callback {
    abstract fun onStart()

    abstract fun onSuccess(content: String)

    abstract fun onFinish()

    abstract fun onFailure(e: Throwable, content: String)

}