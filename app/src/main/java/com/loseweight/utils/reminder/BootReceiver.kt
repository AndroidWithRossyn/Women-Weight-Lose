package com.loseweight.utils.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import java.util.*

class BootReceiver : BroadcastReceiver() {

    private var mRepeatNo: String? = null
    private var mRepeatType: String? = null
    private var mActive: String? = null
    private var mRepeat: String? = null


    private var mReceivedID = 0
    private var mRepeatTime: Long = 0
    private var mCalendar: Calendar? = null
    private var mAlarmReceiver: AlarmReceiver? = null
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("TAG", "onReceive:inside:::: " )
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val rb = DataHelper(context)
            mCalendar = Calendar.getInstance()
            mAlarmReceiver = AlarmReceiver()
            val reminders = rb.getRemindersList()
            Log.e("TAG", "onReceive::::Boot:: ")
            for (rm in reminders) {
                mReceivedID = rm.rId.toInt()
                mRepeat = "true"
                mRepeatNo = "1"
                mRepeatType = "Day"
                mActive = rm.isActive

                val time = Utils.parseTime(rm.remindTime, "hh:mm")

                var cal = Calendar.getInstance()
                cal.time = time;

                mCalendar!!.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
                mCalendar!!.set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
                mCalendar!!.set(Calendar.SECOND, 0)

                // Cancel existing notification of the reminder by using its ID
                // mAlarmReceiver.cancelAlarm(context, mReceivedID);

                // Check repeat type
                if (mRepeatType == "Minute") {
                    mRepeatTime = mRepeatNo!!.toInt() * milMinute
                } else if (mRepeatType == "Hour") {
                    mRepeatTime = mRepeatNo!!.toInt() * milHour
                } else if (mRepeatType == "Day") {
                    mRepeatTime = mRepeatNo!!.toInt() * milDay
                } else if (mRepeatType == "Week") {
                    mRepeatTime = mRepeatNo!!.toInt() * milWeek
                } else if (mRepeatType == "Month") {
                    mRepeatTime = mRepeatNo!!.toInt() * milMonth
                }

                // Create a new notification
                if (mActive == "true") {
                    if (mRepeat == "true") {
                        mAlarmReceiver!!.setRepeatAlarm(context, mCalendar!!, mReceivedID, mRepeatTime)
                    } else if (mRepeat == "false") {
                        mAlarmReceiver!!.setAlarm(context, mCalendar!!, mReceivedID)
                    }
                }
            }
        }
    }

    companion object {
        // Constant values in milliseconds
        private const val milMinute = 60000L
        private const val milHour = 3600000L
        private const val milDay = 86400000L
        private const val milWeek = 604800000L
        private const val milMonth = 2592000000L
    }
}