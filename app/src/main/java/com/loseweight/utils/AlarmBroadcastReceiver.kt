package com.loseweight.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import androidx.core.content.ContextCompat
import com.loseweight.HomeActivity
import com.loseweight.R
import com.loseweight.objects.ReminderTableClass
import com.utillity.db.DataHelper

import java.text.SimpleDateFormat
import java.util.*


class AlarmBroadcastReceiver : BroadcastReceiver() {

    lateinit var dataBaseHelper: DataHelper
    lateinit var reminderClass: ReminderTableClass
    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("<><><><>", "Broadcast Called")
        dataBaseHelper = DataHelper(context)
        val id = intent.getStringExtra(Constant.extraReminderId)
        reminderClass = dataBaseHelper.getReminderById(id!!)
        if (reminderClass.isActive == "true") {

            var arrOfDays = ArrayList<String>()
            if (reminderClass.days.contains(",")) {
                arrOfDays = (reminderClass.days.split(",")) as ArrayList<String>
            } else {
                arrOfDays.add(reminderClass.days)
            }

//            arrOfDays.removeAt(arrOfDays.size - 1)
            for (i in 0 until arrOfDays.size) {
                arrOfDays[i] = arrOfDays[i].replace("'", "")
            }

            val dayNumber = getDayNumber(getCurrentDayName().toUpperCase())
            if (arrOfDays.contains(dayNumber)) {
                fireNotification(context, reminderClass)
            }
        }
    }

    private fun fireNotification(context: Context, reminderClass: ReminderTableClass) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = reminderClass.rId
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelName = context.resources.getString(R.string.app_name)
        val channelDescription = "Application_name Alert"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.description = channelDescription
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_notifications_active)
            builder.color = ContextCompat.getColor(context,R.color.primary)
        } else {
            builder.setSmallIcon(R.drawable.ic_notifications_active)
        }

        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Your body needs energy! You haven't exercised in ${getCurrentFullDayName()}!"))
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        builder.setContentTitle(context.resources.getString(R.string.app_name))
//        builder.setContentText("Your body needs energy! You haven't exercised in ${getCurrentDayName().toUpperCase()}!")
        builder.setAutoCancel(true)
        builder.setOngoing(false)

        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0,notificationIntent, 0)
        builder.setContentIntent(intent)

        notificationManager.notify(reminderClass.rId.toInt(), builder.build())

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(date: String): Date {
        val simpleDateFormat = SimpleDateFormat("dd MMM, yyyy")
        val resultDate = simpleDateFormat.parse(date);
        return resultDate
    }

    private fun isDateBetweenStartEndDate(max: Date, date: Date): Boolean {
        var isDateBetweenToDate = false;
        var currentDate = getCurrentDate()
        var maxDate = getEndDate(max)

        if (currentDate == maxDate) {
            isDateBetweenToDate = true
        } else if (date <= max) {
            isDateBetweenToDate = true
        }
        return isDateBetweenToDate

    }

    private fun getCurrentFullDayName(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getCurrentDayName(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getEndDate(date: Date): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getDayNumber(dayName: String): String {
        var dayNumber = ""
        when (dayName) {
            "SUN" -> dayNumber = "1"
            "MON" -> dayNumber = "2"
            "TUE" -> dayNumber = "3"
            "WED" -> dayNumber = "4"
            "THU" -> dayNumber = "5"
            "FRI" -> dayNumber = "6"
            "SAT" -> dayNumber = "7"
        }
        return dayNumber
    }

}
