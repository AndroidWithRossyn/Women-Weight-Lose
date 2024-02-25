package com.loseweight

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.compactcalender.CompactCalendarView
import com.common.compactcalender.Event
import com.google.gson.Gson
import com.loseweight.adapter.HistoryExpandableAdapter
import com.loseweight.databinding.ActivityHistoryBinding
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.utils.Utils
import com.loseweight.objects.HistoryDetailsClass
import com.loseweight.utils.Constant
import java.text.SimpleDateFormat
import java.util.*


class HistoryActivity : BaseActivity() {

    var binding: ActivityHistoryBinding? = null
    var historyExpandableAdapter: HistoryExpandableAdapter? = null
    private val dateFormatForMonth = SimpleDateFormat("MMM - yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            com.loseweight.utils.Debug.e("","history activity:::::===>>  $intent")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isBackShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.history)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        historyExpandableAdapter = HistoryExpandableAdapter(this,arrayListOf())
        binding!!.rvHistory.layoutManager = LinearLayoutManager(this)
        binding!!.rvHistory.adapter = historyExpandableAdapter

        historyExpandableAdapter!!.setEventListener(object : HistoryExpandableAdapter.EventListener {
            override fun OnMenuClick(parentPosition: Int, childPosition: Int) {

                val childItem = historyExpandableAdapter!!.getMenuSubItem(parentPosition,childPosition)

                when {
                    childItem!!.planDetail!!.hasSubPlan -> {
                        val i = Intent(this@HistoryActivity, FastWorkOutDetailActivity::class.java)
                        i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                        i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                        i.putExtra("day_name", childItem!!.DayName)
                        startActivity(i)
                    }
                    childItem.planDetail!!.planDays.equals("YES") -> {

                        val intent = Intent(this@HistoryActivity, ExercisesListActivity::class.java)
                        intent.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                        intent.putExtra(Constant.extra_day_id, childItem!!.DayId)
                        intent.putExtra("day_name", childItem!!.DayName)
                        intent.putExtra("Week_name", childItem!!.WeekName)
                        startActivity(intent)


                        /*val i = Intent(this@HistoryActivity, DaysPlanDetailActivity::class.java)
                        i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                        i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                        i.putExtra("day_name", childItem!!.DayName)
                        startActivity(i)*/
                    }
                    else -> {
                        val i = Intent(this@HistoryActivity, ExercisesListActivity::class.java)
                        i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                        i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                        i.putExtra("day_name", childItem!!.DayName)
                        startActivity(i)
                    }
                }

            }

            override fun OnMoreClick(parentPosition: Int, childPosition: Int, view: View) {

                val childItem = historyExpandableAdapter!!.getMenuSubItem(parentPosition,childPosition)
                showDeletePopupMenu(view,childItem!!)
            }

        })

       fillData()
    }

    fun fillData()
    {
        val arrHistoryData = dbHelper.getWeekDayOfHistory()
        historyExpandableAdapter!!.addAll(arrHistoryData)
        historyExpandableAdapter!!.expandAllParents()

        compactCalendarSetup()
    }

    private fun showDeletePopupMenu(
        view: View,
        historyDetail: HistoryDetailsClass
    ) {
        val menu = PopupMenu(this, view)

        val s = SpannableString(getString(R.string.delete))
        s.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.md_red_500)),
            0,
            s.length,
            0
        )
        menu.getMenu().add(s)

        menu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {

                if (item!!.title is SpannableString) {
                    showDeleteConfirmationDialog(historyDetail)
                }
                return true
            }

        })

        menu.show()
    }

    private fun showDeleteConfirmationDialog(historyDetail: HistoryDetailsClass) {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.delete_confirmation_msg))
        builder.setPositiveButton(R.string.delete) { dialog, which ->
            showDialog("")
            dbHelper.deleteHistory(historyDetail.HID)
            fillData()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                dismissDialog()
            },1500)
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()
    }

    private fun compactCalendarSetup() {

        binding!!.compatCalenderView.removeAllEvents()
        binding!!.compatCalenderView.shouldScrollMonth(false)

        binding!!.tvMonthYear.text = dateFormatForMonth.format(Calendar.getInstance().time)

        val arrCompleteExerciseDt: ArrayList<String> = dbHelper.getCompleteExerciseDate()

        for (i in 0 until arrCompleteExerciseDt.size) {
            addEvents(Utils.parseTime(arrCompleteExerciseDt[i],Constant.DATE_TIME_24_FORMAT).time)
        }

        binding!!.compatCalenderView.setCurrentDate(Date())
        binding!!.compatCalenderView.setListener(object : CompactCalendarView.CompactCalendarViewListener {

            override fun onDayClick(dateClicked: Date?) {

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                binding!!.tvMonthYear.text = dateFormatForMonth.format(firstDayOfNewMonth!!)
            }
        })

    }

    /* Add Events */
    private fun addEvents(timeInMillis: Long) {
        val currentCalender = Calendar.getInstance(Locale.ENGLISH)
        currentCalender.time = Date()

        binding!!.compatCalenderView.addEvent(Event(Color.argb(255, 237, 55, 221), timeInMillis, "Event at " + Date(timeInMillis)))

    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onNextClick()
        {
            binding!!.compatCalenderView.scrollRight()
        }

        fun onPrevClick()
        {
            binding!!.compatCalenderView.scrollLeft()
        }

    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
               finish()
            }

        }
    }

}
