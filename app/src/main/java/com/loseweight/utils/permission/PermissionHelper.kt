package com.admision.apputils.permission

import android.app.Activity
import android.os.Build
import com.loseweight.utils.permission.ManagePermissionsImp


class PermissionHelper {
    companion object {
        private val permissionsRequestCode = 123
        private lateinit var managePermissions: ManagePermissionsImp
        private lateinit var iPermission: ManagePermissionsImp.IPermission

        fun checkPermissions(activity: Activity, list: List<String>, iPermission: ManagePermissionsImp.IPermission) {
            this.iPermission = iPermission
            managePermissions = ManagePermissionsImp(
                activity,
                list,
                permissionsRequestCode
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                managePermissions.checkPermissions(iPermission)
            }

        }

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                       grantResults: IntArray) {
            when (requestCode) {
                permissionsRequestCode -> {
                    if (managePermissions.processPermissionsResult(permissionsRequestCode, permissions, grantResults)) {
                        iPermission.onPermissionGranted()
                    } else {
                        iPermission.onPermissionDenied()
                    }
                    return
                }
            }
        }
    }
}