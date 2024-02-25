package com.loseweight.utils.watertracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

internal object AlarmHelper {

    fun setNotificationsAlarm(mContext: Context) {
        val isAlarmUp = PendingIntent.getBroadcast(
            mContext, 101, Intent(
                mContext,
                WaterTrackerBroadcastReceiver::class.java
            ), PendingIntent.FLAG_MUTABLE
        ) != null
        if (isAlarmUp) {
            stopNotificationsAlarm(mContext)
        }


        val from = "8:0"
        val interval = 2 // In Hours
        val values = from!!.split(":".toRegex()).toTypedArray()
        val hr = values[0]
        val mt = values[1]
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = Integer.valueOf(hr)
        calendar[Calendar.MINUTE] = Integer.valueOf(mt)
        calendar[Calendar.SECOND] = 0
        val nIntent = Intent(mContext, WaterTrackerBroadcastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(mContext, 101, nIntent,
                PendingIntent.FLAG_MUTABLE)
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (now.after(calendar)) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                now.timeInMillis + 10000,
                1000 * 60 * 60 * interval.toLong(),
                pendingIntent
            )
        } else {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * 60 * interval.toLong(),
                pendingIntent
            )
        }
    }


    fun stopNotificationsAlarm(mContext: Context) {
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(mContext, WaterTrackerBroadcastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(mContext, 101,
                mIntent, PendingIntent.FLAG_MUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    fun setCancelNotificationAlarm(context: Context) {

        val to = "20:0"
        val values = to!!.split(":".toRegex()).toTypedArray()
        val hr = values[0]
        val mt = values[1]
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = Integer.valueOf(hr)
        cal[Calendar.MINUTE] = Integer.valueOf(mt)
        cal[Calendar.SECOND] = 0
        val nIntent = Intent(
            context,
            WaterTrackerStopBroadcastReceiver::class.java
        )
        val pendingIntent =
            PendingIntent.getBroadcast(context, 102, nIntent,
                PendingIntent.FLAG_MUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, cal.timeInMillis] = pendingIntent
    }

}