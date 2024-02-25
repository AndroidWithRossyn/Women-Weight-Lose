package com.loseweight.exceptions

import com.loseweight.utils.Debug

class PermissionException : BaseException() {

    override fun printStackTrace() {
        super.printStackTrace()
        Debug.e("Permission","Permission denied" )
    }
}