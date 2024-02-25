package com.loseweight.utils.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManagePermissionsImp(val activity: Activity,val list: List<String>,val code:Int) {

    // Check permissions at runtime
    fun checkPermissions(permissionable: IPermission) {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            //activity.toast("Permissions already granted.")
            permissionable.onPermissionGranted()
        }
    }


    // Check permissions status
    fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }


    // Show alert dialog to request permissions
   /* private fun showAlert(permissionable: IPermission) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.need_permission_title))
        builder.setMessage(activity.getString(R.string.ask_permission_msg))
        builder.setPositiveButton(activity.getString(R.string.btn_ok),{ dialog,which -> requestPermissions() })
        builder.setNeutralButton(activity.getString(R.string.btn_cancel),{ dialog,which -> permissionable.onPermissionGranted() })
        val dialog = builder.create()
        dialog.show()
    }*/


    // Request the permissions at run time
    private fun requestPermissions() {
        val permission = deniedPermission()
        if(permission != ""){
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }


    // Process permissions result
    fun processPermissionsResult(requestCode: Int, permissions: Array<String>,
                                 grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }

        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }

    interface IPermission {
        fun onPermissionGranted()

        fun onPermissionDenied()
    }
}