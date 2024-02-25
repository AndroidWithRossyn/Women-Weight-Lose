package com.loseweight

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.ReminderAdapter
import com.loseweight.databinding.ActivityReminderBinding
import com.loseweight.databinding.ActivityRestDayBinding
import com.loseweight.interfaces.DateEventListener
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import java.util.*


class RestDayActivity : BaseActivity() {

    var binding: ActivityRestDayBinding? = null
    var workoutPlanData: HomePlanTableClass? = null
    var dayId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rest_day)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        initBack()
        init()
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomePlanTableClass>() {}.type)!!
                    dayId = intent.getStringExtra(Constant.extra_day_id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.tvTitleText.text = getString(R.string.rest_day)
        binding!!.topbar.isBackShow = true
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

    }



    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onFinishedClick() {
            dayId?.let { dbHelper.updatePlanDayCompleteByDayId(it) }
            finish()
        }
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
