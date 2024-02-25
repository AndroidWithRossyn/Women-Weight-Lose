package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.adapter.WhatsYourGoalAdapter
import com.loseweight.databinding.ActivityChooseYourPlanBinding
import com.loseweight.databinding.ActivityTurnOnWaterBinding
import com.loseweight.databinding.ActivityWhatsYourGoalBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.loseweight.utils.watertracker.AlarmHelper
import java.util.*


class TurnOnWaterActivity : BaseActivity() {
    val TAG = TurnOnWaterActivity::class.java.name + Constant.ARROW

    var binding: ActivityTurnOnWaterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_turn_on_water)
        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        init()
    }


    private fun init() {

        binding!!.llTurnOnWaterTracker.setOnClickListener {
            val currDate = Utils.parseTime(Date().time, "dd-MM-yyyy")
            Utils.setPref(this, Constant.PREF_IS_WATER_TRACKER_ON, true)
            Utils.setPref(this, Constant.PREF_WATER_TRACKER_DATE, currDate)
            Utils.setPref(this, Constant.PREF_WATER_TRACKER_GLASS, 0)

            AlarmHelper.setNotificationsAlarm(this)
            AlarmHelper.setCancelNotificationAlarm(this)

            val i = Intent(this, WaterTrackerActivity::class.java)
            startActivity(i)
            finish()
        }

        binding!!.imgBack.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
