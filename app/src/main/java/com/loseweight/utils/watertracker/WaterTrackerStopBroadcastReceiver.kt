package com.loseweight.utils.watertracker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.loseweight.HomeActivity
import com.loseweight.R
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.watertracker.AlarmHelper
import com.utillity.db.DataHelper
import java.text.SimpleDateFormat
import java.util.*


class WaterTrackerStopBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        AlarmHelper.stopNotificationsAlarm(context)
        println("fired!")
    }
}
