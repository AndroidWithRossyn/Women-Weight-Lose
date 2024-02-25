/*
package com.texon.apputils.service

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Debug
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class DownloadService : Service() {

    private val TAG = "DownloadService"
    private var isJobRunning = false

    //    CategoryVideoListRes.Datum catRes = null;

    override fun onCreate() {
        super.onCreate()

    }


    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?,flags: Int,startId: Int): Int {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        if (!isJobRunning) {
            if (intent != null) {
                startJob(intent,startId)
            } else {
                stopSelf(startId)
            }
        }
        return Service.START_NOT_STICKY
    }

    private fun startJob(intent: Intent,startId: Int) {
        isJobRunning = true
        try {

            if (intent.extras != null) {


            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        isJobRunning = false

    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Debug.e(TAG,"onTaskRemoved() called with: rootIntent = [$rootIntent]")
        isJobRunning = false

    }

}*/
