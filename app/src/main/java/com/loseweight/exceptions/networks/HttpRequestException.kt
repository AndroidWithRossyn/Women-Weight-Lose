package com.loseweight.exceptions.networks


import com.loseweight.utils.Debug

class HttpRequestException : NetworkException() {
    override var errMessage: String
        get() = "Server not responding"
        set(value) {}

    override var title: String
        get() = "Server Error"
        set(value) {}

    override fun printStackTrace() {
        super.printStackTrace()
        Debug.e("Server Error","Server not responding")

    }

    override fun getLocalizedMessage(): String {
        return super.getLocalizedMessage()
    }
}