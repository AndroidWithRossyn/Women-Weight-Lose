package com.loseweight.interfaces

interface PermissionListener {

    fun onPermissionClick()

    fun onPermissionAllow(isAllow: Boolean)
}