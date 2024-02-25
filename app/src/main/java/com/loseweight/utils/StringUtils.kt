package com.loseweight.utils



object StringUtils {
    fun isEmpty(str: String?): Boolean {
        if (str != null && !str.isEmpty())
            return false
        return true
    }
}