package com.loseweight.utils.watertracker

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.loseweight.HomeActivity
import com.loseweight.R
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.Constant
import com.utillity.db.DataHelper
import java.util.*


class WaterTrackerBroadcastReceiver : BroadcastReceiver() {

    lateinit var dataBaseHelper: DataHelper
    lateinit var reminderClass: ReminderTableClass
    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {

        val NOTIFICATION_ID = System.currentTimeMillis().toInt()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val rightNow = Calendar.getInstance()
        val currentHourIn24Format = rightNow[Calendar.HOUR_OF_DAY]

        if(currentHourIn24Format in 8..22) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val importance = NotificationManager.IMPORTANCE_HIGH
                val channelName = context.resources.getString(R.string.app_name)
                val channelDescription = context.getString(R.string.time_to_drink_water)
                val mChannel =
                    NotificationChannel("".plus(NOTIFICATION_ID), channelName, importance)
                mChannel.description = channelDescription
                mChannel.enableVibration(true)
                notificationManager.createNotificationChannel(mChannel)
            }

            val builder = NotificationCompat.Builder(context, "".plus(NOTIFICATION_ID))
            val collapsedView = RemoteViews(context.packageName, R.layout.item_notification_coll)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.ic_notifications_active)
                builder.color = context.resources.getColor(R.color.primary)

                collapsedView.setImageViewResource(R.id.big_icon, R.drawable.wt_notification_drink)
                collapsedView.setTextViewText(R.id.content_title, context.getString(R.string.time_to_hydrate))
                collapsedView.setTextViewText(
                    R.id.content_text,
                    context.getString(R.string.drink_water_make_skin_batter)

                )
                collapsedView.setTextViewText(
                    R.id.timestamp,
                    ""
                )
            }

            val mIntent = Intent(context, HomeActivity::class.java)
            mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            mIntent.putExtra("isFrom", Constant.FROM_DRINK_NOTIFICATION)
            val pendingIntent =
                PendingIntent.getActivity(context, 0,
                    mIntent, PendingIntent.FLAG_MUTABLE)
            val icon = BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_launcher
            )

            builder.setCustomContentView(collapsedView)
            builder.setShowWhen(false)
            builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setAutoCancel(true)
            builder.setVisibility(NotificationCompat.VISIBILITY_SECRET)
            builder.setContentIntent(pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }

    }

    private fun setupNotification(context: Context): NotificationCompat.Builder {
        //  Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.drop_notification_icon);
        val builder = NotificationCompat.Builder(context, context.getString(R.string.app_name))
        builder.setSmallIcon(R.drawable.ic_notifications_active).setContentTitle("Water Tracker")
            .setContentText("It is time to drink water")
            .setContentInfo("add a drink!").setAutoCancel(true)
        builder.setDefaults(Notification.DEFAULT_SOUND)
        return builder
    }

}
