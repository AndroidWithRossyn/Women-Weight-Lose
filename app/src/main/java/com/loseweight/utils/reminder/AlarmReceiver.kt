package com.loseweight.utils.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.loseweight.HomeActivity
import com.loseweight.R
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.Constant
import com.utillity.db.DataHelper
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    var mAlarmManager: AlarmManager? = null
    var mPendingIntent: PendingIntent? = null
    lateinit var dataBaseHelper: DataHelper
    lateinit var reminderClass: ReminderTableClass

    override fun onReceive(context: Context, intent: Intent) {
        dataBaseHelper = DataHelper(context)
        val id = intent.getStringExtra(Constant.EXTRA_REMINDER_ID)
        Log.e("TAG", "onReceive:Start ServiceL:::: $id")
        try {
            reminderClass = dataBaseHelper.getReminderById(id!!)
            if (reminderClass.isActive == "true") {

                var arrOfDays = ArrayList<String>()
                if (reminderClass.days.contains(",")) {
                    arrOfDays = (reminderClass.days.split(",")) as ArrayList<String>
                } else {
                    arrOfDays.add(reminderClass.days)
                }

                for (i in 0 until arrOfDays.size) {
                    arrOfDays[i] = arrOfDays[i].replace("'", "")
                    Log.e("TAG", "onReceive:Arraydays:::::  " + arrOfDays[i])
                }

                val dayNumber = getDayNumber(getCurrentDayName().toUpperCase())
                Log.e("TAG", "onReceive::::Day Number::::: $dayNumber")
                if (arrOfDays.contains(dayNumber)) {
                    fireNotification(context, reminderClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setAlarm(context: Context, calendar: Calendar, ID: Int) {
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Put Reminder ID in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        intent.putExtra(Constant.EXTRA_REMINDER_ID, Integer.toString(ID))
//        mPendingIntent = PendingIntent.getBroadcast(context, intent_id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
//        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent,
            PendingIntent.FLAG_IMMUTABLE)
//        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Calculate notification time
        val c = Calendar.getInstance()
        val currentTime = c.timeInMillis
        val diffTime = calendar.timeInMillis - currentTime

        /*         Start alarm using notification time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("TAG", "setAlarm::::Ifff::: " )
            mAlarmManager!!.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime, mPendingIntent)
        } else {
            Log.e("TAG", "setAlarm:::ElsE:::::::: " )
            mAlarmManager!!.setExact(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime, mPendingIntent);
        }*/

//        mAlarmManager!![AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = mPendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(intent)
                }
            }
        }
        mAlarmManager!!.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,mPendingIntent!!)

//        mAlarmManager!![AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime] = mPendingIntent

        // Restart alarm if device is rebooted
        Log.e("TAG", "setAlarm::::::::::SET $ID  ${SystemClock.elapsedRealtime() + diffTime}   ${SystemClock.elapsedRealtime()}  $diffTime"  )

        /*if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mAlarmManager!!.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime, mPendingIntent);
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mAlarmManager!!.setExact(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime, mPendingIntent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlarmManager!!.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime, mPendingIntent)
            };
        }*/

        // Restart alarm if device is rebooted
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )


    }

    fun setRepeatAlarm(context: Context, calendar: Calendar, ID: Int, RepeatTime: Long) {
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent_id = System.currentTimeMillis().toInt()
        // Put Reminder ID in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        intent.putExtra(Constant.EXTRA_REMINDER_ID, Integer.toString(ID))
//        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        mPendingIntent = PendingIntent.getBroadcast(context, intent_id,
            intent, PendingIntent.FLAG_MUTABLE)
//        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Calculate notification timein
        val c = Calendar.getInstance()
        val currentTime = c.timeInMillis
        val diffTime = calendar.timeInMillis - currentTime

        // Start alarm using initial notification time and repeat interval time
        /* mAlarmManager!!.setRepeating(
             AlarmManager.ELAPSED_REALTIME,
             SystemClock.elapsedRealtime() + diffTime,
             RepeatTime, mPendingIntent
         )*/
        mAlarmManager!!.setRepeating(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + diffTime,
            RepeatTime, mPendingIntent!!
        )

        /* if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
             mAlarmManager!!.set(AlarmManager.RTC_WAKEUP, diffTime, mPendingIntent);
         } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
             mAlarmManager!!.setExact(AlarmManager.RTC_WAKEUP, diffTime, mPendingIntent);
         } else {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 mAlarmManager!!.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, diffTime, mPendingIntent)
             };
         }*/

        // Restart alarm if device is rebooted
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm(context: Context, ID: Int) {
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID,
            Intent(context, AlarmReceiver::class.java),PendingIntent.FLAG_IMMUTABLE )
        mAlarmManager!!.cancel(mPendingIntent!!)

        // Disable alarm
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }


    private fun fireNotification(context: Context, reminderClass: ReminderTableClass) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = reminderClass.rId
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelName = context.resources.getString(R.string.app_name)
        val channelDescription = "Application_name Alert"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId.toString(), channelName, importance)
            mChannel.description = channelDescription
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context, channelId.toString())
        builder.setSmallIcon(R.drawable.ic_notifications_active)
        builder.color = ContextCompat.getColor(context, R.color.primary)

        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Your body needs energy! You haven't exercised in ${getCurrentFullDayName()}!"))
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        builder.setContentTitle(context.resources.getString(R.string.app_name))
        builder.setAutoCancel(true)
        builder.setOngoing(false)

        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
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
            "MON" -> dayNumber = "1"
            "TUE" -> dayNumber = "2"
            "WED" -> dayNumber = "3"
            "THU" -> dayNumber = "4"
            "FRI" -> dayNumber = "5"
            "SAT" -> dayNumber = "6"
            "SUN" -> dayNumber = "7"
        }
        return dayNumber
    }
}