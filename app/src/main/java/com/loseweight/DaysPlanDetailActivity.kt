package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.DaysPlanDetailExpandableAdapter
import com.loseweight.databinding.ActivityDaysPlanDetailBinding
import com.loseweight.dialog.ReplaceExerciseDialogFragment
import com.loseweight.dialog.RestartDayDialogFragment
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.objects.PWeeklyDayData
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import java.io.Serializable


class DaysPlanDetailActivity : BaseActivity() {

    var binding: ActivityDaysPlanDetailBinding? = null
    var daysPlanDetailExpandableAdapter: DaysPlanDetailExpandableAdapter? = null
    private var workoutPlanData: HomePlanTableClass? = null
    lateinit var weeklyDayStatusList: ArrayList<PWeeklyDayData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_days_plan_detail)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomePlanTableClass>() {}.type)!!
                    //binding!!.llChangePlan.visibility = View.GONE
                }else{
                    //binding!!.llChangePlan.visibility = View.VISIBLE
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()

        var isShow = true
        var scrollRange = -1
        binding!!.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding!!.llTopTitle.visibility = View.VISIBLE
                binding!!.imgBack.setColorFilter(
                    ContextCompat.getColor(this, R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                isShow = true
            } else if (isShow) {
                binding!!.imgBack.setColorFilter(
                    ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                binding!!.llTopTitle.visibility = View.GONE //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })

        daysPlanDetailExpandableAdapter = DaysPlanDetailExpandableAdapter(this, arrayListOf())
        binding!!.rvDays.layoutManager = LinearLayoutManager(this)
        binding!!.rvDays.setAdapter(daysPlanDetailExpandableAdapter)

        daysPlanDetailExpandableAdapter!!.setEventListener(object :
            DaysPlanDetailExpandableAdapter.EventListener {
            override fun OnDayClick(parentPosition: Int, childPosition: Int) {
                startWorkOut(parentPosition, childPosition)
            }
        })


        fillData()

    }

    private fun startWorkOut(position: Int, child: Int) {
        val item = daysPlanDetailExpandableAdapter!!.getItem(position)!!.arrWeekDayData.get(child)

        if(item.Workouts?.toFloat() ?: 0f > 0) {

            workoutPlanData!!.planMinutes = item.Minutes
            workoutPlanData!!.planWorkouts = item.Workouts
            val intent = Intent(this@DaysPlanDetailActivity, ExercisesListActivity::class.java)
            intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            intent.putExtra(Constant.extra_day_id, item.Day_id)
            intent.putExtra("day_name", item.Day_name)
            intent.putExtra(
                "Week_name",
                daysPlanDetailExpandableAdapter!!.getItem(position)?.Week_name
            )
            startActivity(intent)
        }else {
            val intent = Intent(this@DaysPlanDetailActivity, RestDayActivity::class.java)
            intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            intent.putExtra(Constant.extra_day_id, item.Day_id)
            intent.putExtra("day_name", item.Day_name)
            intent.putExtra(
                "Week_name",
                daysPlanDetailExpandableAdapter!!.getItem(position)?.Week_name
            )
            startActivity(intent)
        }
    }


    private fun fillData() {

        var str = Utils.getPref(this,Constant.PREF_GOAL,"")
        if(str.isNullOrEmpty().not())
        {
            workoutPlanData = Gson().fromJson(str, object : TypeToken<HomePlanTableClass>() {}.type)!!
        }

        if (workoutPlanData != null) {
            binding!!.tvTitleText.text = workoutPlanData!!.planName
            binding!!.tvTitle.text = workoutPlanData!!.planName
            binding!!.imgCover.setImageResource(Utils.getDrawableId(workoutPlanData!!.planImage,this))
           // binding!!.tvIntroductionDes.text = workoutPlanData!!.introduction

            weeklyDayStatusList = dbHelper.getWorkoutWeeklyData(workoutPlanData!!.planName!!,workoutPlanData!!.planId!!)
            daysPlanDetailExpandableAdapter!!.addAll(weeklyDayStatusList)

            Utils.setDayProgressData(
                this,
                workoutPlanData!!,
                binding!!.tvDaysLeft,
                null,
                binding!!.pbDay
            )
        }

        binding!!.nestedScrollView.smoothScrollTo(0,0)
    }

    override fun onResume() {
        super.onResume()
        fillData()
    }


    inner class ClickHandler {

        fun onReStartClick() {
            showRestartProgressDialog()
        }

        fun onChangePlanClick() {
            val i = Intent(this@DaysPlanDetailActivity, WhatsYourGoalActivity::class.java)
            startActivity(i)
        }

        fun onBackClick() {
            finish()
        }
    }

    private fun showRestartProgressDialog() {
        val restartDayDialog = RestartDayDialogFragment.newInstance(this)

        restartDayDialog.setOnEventListener(object : RestartDayDialogFragment.DialogDismissListener{
            override fun onDismissListener(needToClear: Boolean) {
                if(needToClear) {
                    dbHelper.restartDayPlan(workoutPlanData?.planId!!)
                    fillData()
                }
            }

        })

        restartDayDialog.show(supportFragmentManager)
    }


}
