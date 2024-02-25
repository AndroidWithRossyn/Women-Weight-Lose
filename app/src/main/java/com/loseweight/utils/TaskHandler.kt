package com.loseweight.utils

import android.content.Context
import androidx.databinding.ObservableField

class TaskHandler {

    var taskObservable = ObservableField<Boolean>()

    var isCellActivitySyncingFinished = false
    var isInamateActivitySyncingFinished = false
    var isHeadCountSyncingFinished = false
    var isBeaconActivitySyncingFinished = false


    fun StartAllTask(context: Context) {
        Debug.e("","StartAllTask")
        taskObservable.set(true)
//        taskObservable.notifyChange()
//        taskObservable.notifyPropertyChanged(1)
        /*UploadAllCellActivityImage(context, this).execute()
        UploadAllInmateActivityImage(context, this).execute()
        UploadAllHeadCountImage(context, this).execute()
        InsertBeaconActivityAsync(context, this).execute()*/
    }

    fun onFinishAnyTask() {
        if (isCellActivitySyncingFinished && isInamateActivitySyncingFinished && isBeaconActivitySyncingFinished && isHeadCountSyncingFinished) {
            Debug.e("","AllTaskFinish")
            taskObservable.set(false)
//            taskObservable.notifyChange()
//            taskObservable.notifyPropertyChanged(1)
        }
    }
}