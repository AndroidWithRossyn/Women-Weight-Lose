package com.loseweight.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.*
import com.loseweight.adapter.BodyFocusAdapter
import com.loseweight.databinding.FragmentPlanBinding
import com.loseweight.objects.HistoryDetailsClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import java.util.*

class PlanFragment : BaseFragment() {

    lateinit var binding: FragmentPlanBinding
    var title: String? = null
    var segment: String? = null
    var rootContext: Context? = null
    var mainGoalPlanData: HomePlanTableClass? = null
    var recentPlan: HomePlanTableClass? = null
    var bodyFocusAdapter: BodyFocusAdapter? = null
    var lastWorkout: HistoryDetailsClass? = null


    companion object {
        fun newInstance(title: String, segment: String): PlanFragment {
            val pane = PlanFragment()
            val args = Bundle()
            /*args.putString(Constant.TITLE, title)
            args.putString(Constant.SEGMENT, segment)
            if (mNotificationDataModel != null) {
                args.putString(Constant.NotificationData, Gson().toJson(mNotificationDataModel))
            }*/
            pane.arguments = args
            return pane
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        rootContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan, container, false)
        /*if (arguments != null && arguments!!.getString(Constant.TITLE).isNullOrEmpty().not()) {
            title = arguments!!.getString(Constant.TITLE)
            Debug.e("title", title)
        }

        if (arguments != null && arguments!!.getString(Constant.SEGMENT).isNullOrEmpty().not()) {
            segment = arguments!!.getString(Constant.SEGMENT)
            Debug.e("segment", segment)
        }

        if (arguments != null && arguments!!.getString(Constant.NotificationData).isNullOrEmpty().not()) {
            var notificationData = arguments!!.getString(Constant.NotificationData)
            mNotificationDataModel = Gson().fromJson(JSONObject(notificationData).toString(), object :
                    TypeToken<NotificationDataModel>() {}.type)!!
            Debug.e("notificationData", notificationData)
        }*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    private fun init() {
        binding.handler = ClickHandler()
        binding.rvBodyFocus.setLayoutManager(GridLayoutManager(rootContext, 2))
        bodyFocusAdapter = BodyFocusAdapter(rootContext!!)
        binding.rvBodyFocus.setAdapter(bodyFocusAdapter)
        bodyFocusAdapter!!.setEventListener(object : BodyFocusAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = bodyFocusAdapter!!.getItem(position)
                val i = Intent(rootContext, HomeDetailActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(item))
                startActivity(i)
            }

        })


        fillRecentData()
        fillWaterTracker()
        fillMainGoalData()
        fillBodyFocusData()
    }

    fun fillRecentData()
    {
        lastWorkout = dbHelper.getRecentHistory()
        if (lastWorkout != null) {
            binding!!.llRecent.visibility = View.VISIBLE
            recentPlan = dbHelper.getPlanByPlanId(lastWorkout!!.PlanId.toInt())

            if (recentPlan!!.planDays == Constant.PlanDaysYes) {
                binding!!.tvRecentWorkOutName.text = recentPlan!!.planName
                val item = dbHelper.getDaysPlanData(lastWorkout!!.DayId)
                recentPlan!!.planMinutes = item!!.Minutes
                recentPlan!!.planWorkouts = item!!.Workouts
            } else {
                binding!!.tvRecentWorkOutName.text = lastWorkout!!.PlanName
            }

            if (lastWorkout!!.planDetail?.planDays.equals(Constant.PlanDaysYes)) {
                val compDay =
                    dbHelper.getCompleteDayCountByPlanId(lastWorkout!!.planDetail?.planId!!)
                binding.tvTime.text =
                    (lastWorkout!!.planDetail?.days!!.toInt() - compDay).toString()
                        .plus(" Days left")

            } else {
                binding.tvTime.text = lastWorkout!!.planDetail!!.planMinutes.plus(" Mins")
            }

            binding!!.imgRecentWorkout.setImageResource(
                Utils.getDrawableId(
                    recentPlan!!.planThumbnail,
                    rootContext!!
                )
            )
        } else {
            binding!!.llRecent.visibility = View.GONE
        }
    }

    private fun fillWaterTracker() {
        if (Utils.getPref(rootContext!!, Constant.PREF_IS_WATER_TRACKER_ON, false)) {
            binding.tvWaterTrackerDes.visibility = View.GONE
            binding.btnStartWaterTracker.visibility = View.GONE
            binding.llAfterWaterTrackerOn.visibility = View.VISIBLE
            binding.btnDrink.visibility = View.VISIBLE

            val lastDate = Utils.getPref(rootContext!!,Constant.PREF_WATER_TRACKER_DATE,"")
            val currDate = Utils.parseTime(Date().time,"dd-MM-yyyy")
            if(lastDate.equals(currDate).not())
            {
                Utils.setPref(rootContext!!,Constant.PREF_WATER_TRACKER_DATE,currDate)
                Utils.setPref(rootContext!!,Constant.PREF_WATER_TRACKER_GLASS,0)
                binding.circularProgressBar.progress =  0f
                binding.tvWaterDrinked.text = "0"
            }else{
               binding.circularProgressBar.progress =  Utils.getPref(rootContext!!,Constant.PREF_WATER_TRACKER_GLASS,0).toFloat()
                binding.tvWaterDrinked.text = Utils.getPref(rootContext!!,Constant.PREF_WATER_TRACKER_GLASS,0).toString()
            }

        } else {
            binding.tvWaterTrackerDes.visibility = View.VISIBLE
            binding.btnStartWaterTracker.visibility = View.VISIBLE
            binding.llAfterWaterTrackerOn.visibility = View.GONE
            binding.btnDrink.visibility = View.GONE
            binding.circularProgressBar.progress =  0f
        }
    }

    private fun fillBodyFocusData() {
        bodyFocusAdapter!!.addAll(dbHelper.getHomePlanList(Constant.PlanTypeBodyFocus))
    }

    fun fillMainGoalData() {
        val str = Utils.getPref(rootContext!!, Constant.PREF_GOAL, "")

        if (str.isNullOrEmpty().not()) {
            mainGoalPlanData =
                Gson().fromJson(str!!, object : TypeToken<HomePlanTableClass>() {}.type)

            binding.imgMainGoalPlan.setImageResource(
                Utils.getDrawableId(
                    mainGoalPlanData!!.planImage,
                    rootContext!!
                )
            )
            binding.tvPlanName.text = mainGoalPlanData!!.planName

            Utils.setDayProgressData(
                rootContext!!,
                mainGoalPlanData!!,
                binding.tvDaysLeft,
                null,
                binding.pbDay,
                binding.btnDay
            )

        }

    }

    override fun onResume() {
        super.onResume()
        fillWaterTracker()
        fillMainGoalData()
        fillRecentData()
    }

    inner class ClickHandler {

        fun onPlanChangeClick() {
            val i = Intent(rootContext, WhatsYourGoalActivity::class.java)
            startActivity(i)
        }

        fun onDaysPlanClick() {
            val i = Intent(rootContext, DaysPlanDetailActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(mainGoalPlanData))
            startActivity(i)
        }


        fun onDrinkClick() {
            val i = Intent(rootContext, WaterTrackerActivity::class.java)
            startActivity(i)
        }

        fun onStartWaterClick() {
            val i = Intent(rootContext, TurnOnWaterActivity::class.java)
            startActivity(i)
        }

        fun onFastWorkoutClick() {
            val i = Intent(rootContext, FastWorkoutActivity::class.java)
            startActivity(i)
        }

        fun onRecentViewAllClick() {
            val i = Intent(rootContext, RecentActivity::class.java)
            startActivity(i)

        }

        fun onRecentViewClick() {

            if (recentPlan!!.hasSubPlan) {
                val i = Intent(rootContext, FastWorkOutDetailActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                startActivity(i)
            } else if (recentPlan!!.planDays.equals("YES")) {

                val intent = Intent(context, ExercisesListActivity::class.java)
                intent.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                intent.putExtra(Constant.extra_day_id, lastWorkout!!.DayId)
                intent.putExtra("day_name", lastWorkout!!.DayName)
                intent.putExtra(
                    "Week_name",
                    lastWorkout?.WeekName
                )
                startActivity(intent)

            } else {
                val i = Intent(rootContext, ExercisesListActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                startActivity(i)
            }
        }

    }
}