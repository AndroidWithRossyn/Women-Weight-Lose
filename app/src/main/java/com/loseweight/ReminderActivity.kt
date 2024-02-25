package com.loseweight

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.loseweight.adapter.ReminderAdapter
import com.loseweight.databinding.ActivityReminderBinding
import com.loseweight.interfaces.DateEventListener
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.loseweight.utils.reminder.AlarmReceiver
import com.utillity.db.DataHelper
import java.util.*


class ReminderActivity : BaseActivity() {

    var binding: ActivityReminderBinding? = null
    var reminderAdapter: ReminderAdapter? = null
    private var arrReminder = ArrayList<ReminderTableClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        initBack()
        init()
    }

    private fun initIntentParam() {
        try {
            /*if (intent.extras != null) {
                if (intent.extras!!.containsKey("from")) {
                    val from = intent.getStringExtra("from")
                    if(from.equals(Constant.FROM_SETTING))
                    {
                        initBack()
                        binding!!.topbar.isBackShow = true
                        return
                    }
                }
            }
            binding!!.topbar.isMenuShow = true*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.tvTitleText.text = getString(R.string.reminder)
        binding!!.topbar.isBackShow = true
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        reminderAdapter = ReminderAdapter(this)
        binding!!.rvRecent.layoutManager = LinearLayoutManager(this)
        binding!!.rvRecent.setAdapter(reminderAdapter)

        reminderAdapter!!.setEventListener(object : ReminderAdapter.EventListener {

            override fun onRepeatClick(position: Int, view: View) {
                val item = reminderAdapter!!.getItem(position)
                showDaySelectionDialog(item, true,null,null)
            }

            override fun onTimeClick(position: Int, view: View) {
                val time =
                    Utils.parseTime(reminderAdapter!!.getItem(position)!!.remindTime, "hh:mm")
                showTimePickerDialog(this@ReminderActivity, time, object : DateEventListener {
                    override fun onDateSelected(
                        date: Date,
                        hourOfDay: Int,
                        minute: Int
                    ) {
                       dbHelper.updateReminderTimes(
                            reminderAdapter!!.getItem(position)!!.rId,
                            Utils.parseTime(date.time, "HH:mm")
                        )
                        fillData()
                        setResult(Activity.RESULT_OK)

                        var mCalendarCurrent = Calendar.getInstance()

                        var mDay = mCalendarCurrent.get(Calendar.DATE)

                        if (hourOfDay <= mCalendarCurrent.get(Calendar.HOUR_OF_DAY) && minute < mCalendarCurrent!!.get(Calendar.MINUTE)) {
                            mDay += 1
                        }
                        mCalendarCurrent[Calendar.DAY_OF_MONTH] = mDay
                        mCalendarCurrent[Calendar.HOUR_OF_DAY] = hourOfDay
                        mCalendarCurrent[Calendar.MINUTE] = minute
                        mCalendarCurrent[Calendar.SECOND] = 0

                        AlarmReceiver().setAlarm(this@ReminderActivity,mCalendarCurrent,reminderAdapter!!.getItem(position)!!.rId.toInt())
                    }
                })
            }

            override fun onDeleteClick(position: Int, view: View) {
                confirmDeleteReminder(this@ReminderActivity,getString(R.string.tip),getString(R.string.confirm_delete),position)
            }

            override fun onSwitchChecked(position: Int, isChecked: Boolean, view: View) {
                if (isChecked) {
                    dbHelper.updateReminder(reminderAdapter!!.getItem(position).rId, "true")
                } else {
                    dbHelper.updateReminder(reminderAdapter!!.getItem(position).rId, "false")
                }
            }
        })


        fillData()

    }


    private fun fillData() {
        try {
            arrReminder.clear()
            arrReminder = dbHelper.getRemindersList()
            reminderAdapter!!.addAll(arrReminder)

            showPlaceHolder()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showPlaceHolder() {
        if(reminderAdapter!!.itemCount > 0)
        {
            binding!!.rvRecent.visibility = View.VISIBLE
            binding!!.llPlaceHolder.visibility = View.GONE
        }else {
            binding!!.rvRecent.visibility = View.GONE
            binding!!.llPlaceHolder.visibility = View.VISIBLE
        }
    }

    private fun confirmDeleteReminder(content: Context, strTitle: String, strMsg: String, adapterPosition:Int): Boolean {

        val builder = AlertDialog.Builder(content)
        builder.setTitle(strTitle)
        builder.setMessage(strMsg)
        builder.setCancelable(true)

        builder.setPositiveButton(R.string.btn_ok) { dialog, id ->
            try {
                dbHelper.deleteReminder(reminderAdapter!!.getItem(adapterPosition).rId)
                reminderAdapter!!.removeAt(adapterPosition)
                showPlaceHolder()
                dialog.cancel()
                setResult(Activity.RESULT_OK)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        builder.setNegativeButton(R.string.cancel) { dialog, id ->
            try {
                dialog.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()

        return false
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onAddReminderClick() {
            showTimePickerDialog(this@ReminderActivity, Date(), object : DateEventListener {
                override fun onDateSelected(
                    date: Date,
                    hourOfDay: Int,
                    minute: Int
                ) {
                    showDaySelectionDialog(null, false,hourOfDay,minute)
                }
            })
        }
    }

    private fun showDaySelectionDialog(item: ReminderTableClass?, isFromEdit: Boolean, hourOfDay:Int?, minute:Int?) {

        val daysList = arrayOf<CharSequence>(
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
        )
        var booleanArray = booleanArrayOf(true, true, true, true, true, true, true)
        var arrayList = arrayListOf<String>("1", "2", "3", "4", "5", "6", "7")

        if (isFromEdit) {
            val strDays = item!!.days.split(",").sorted()
            val arrayListBoolean = arrayListOf<Boolean>()
            for (i in 1 until 8) {
                if (strDays.contains(i.toString())) {
                    arrayListBoolean.add(true)
                } else {
                    arrayList.remove(i.toString())
                    arrayListBoolean.add(false)
                }
            }


              arrayListBoolean.toBooleanArray()
        }

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setTitle(R.string.repeat)
        builder.setMultiChoiceItems(
            daysList,
            booleanArray,
            object : DialogInterface.OnMultiChoiceClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
                    if (isChecked && arrayList.contains((which + 1).toString()).not()) {
                        arrayList.add((which + 1).toString())
                    } else {
                        arrayList.remove((which + 1).toString())
                    }
                }
            })

        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->

            if (isFromEdit) {
                dbHelper.updateReminderDays(item!!.rId, arrayList.joinToString(","))
            } else {

                val reminderClass = ReminderTableClass()
                reminderClass.remindTime = String.format("%02d:%02d", hourOfDay, minute)

                reminderClass.days = arrayList.joinToString(",")
                reminderClass.isActive = "true"

                val mCount = DataHelper(this).addReminder(reminderClass)

                var mCalendarCurrent = Calendar.getInstance()

                var mDay = mCalendarCurrent.get(Calendar.DATE)

                if (hourOfDay!! <= mCalendarCurrent.get(Calendar.HOUR_OF_DAY) && minute!! <= mCalendarCurrent!!.get(Calendar.MINUTE)) {
                    mDay += 1
                }else if(!arrayList.contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()))
                    mDay += 1

                mCalendarCurrent[Calendar.DAY_OF_MONTH] = mDay
                mCalendarCurrent[Calendar.HOUR_OF_DAY] = hourOfDay
                mCalendarCurrent[Calendar.MINUTE] = minute!!
                mCalendarCurrent[Calendar.SECOND] = 0

                AlarmReceiver().setAlarm(this@ReminderActivity,mCalendarCurrent,mCount)

                Utils.setPref(this, Constant.PREF_IS_REMINDER_SET, true)
            }
            fillData()
            setResult(Activity.RESULT_OK)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()

    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                finish()
                setResult(Activity.RESULT_OK)
            }

        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

}
