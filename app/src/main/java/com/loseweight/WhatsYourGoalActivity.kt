package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.adapter.WhatsYourGoalAdapter
import com.loseweight.databinding.ActivityChooseYourPlanBinding
import com.loseweight.databinding.ActivityWhatsYourGoalBinding
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils


class WhatsYourGoalActivity : BaseActivity() {
    val TAG = WhatsYourGoalActivity::class.java.name + Constant.ARROW

    var binding: ActivityWhatsYourGoalBinding? = null
    var whatsYourGoalAdapter: WhatsYourGoalAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whats_your_goal)
        init()
    }


    private fun init() {

        binding!!.mRecyclerView.setLayoutManager(LinearLayoutManager(this))
        whatsYourGoalAdapter = WhatsYourGoalAdapter(this)
        binding!!.mRecyclerView.adapter = whatsYourGoalAdapter
        whatsYourGoalAdapter!!.setEventListener(object : WhatsYourGoalAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                Utils.setPref(this@WhatsYourGoalActivity, Constant.PREF_IS_FIRST_TIME, false)
                val item = whatsYourGoalAdapter!!.getItem(position)
                whatsYourGoalAdapter!!.changeSelection(position)
                Utils.setPref(this@WhatsYourGoalActivity,Constant.PREF_GOAL,Gson().toJson(item))
               finish()
            }
        })

        binding!!.imgBack.setOnClickListener {
            finish()
        }

        addData()
    }

    private fun addData() {
        var list =  dbHelper.getHomePlanList(Constant.PlanTypeMainGoals)

        whatsYourGoalAdapter!!.addAll(list)
        val str  = Utils.getPref(this,Constant.PREF_GOAL,"")

        if(str.isNullOrEmpty().not())
        {
            var planData:HomePlanTableClass = Gson().fromJson(str!!, object : TypeToken<HomePlanTableClass>() {}.type)
            whatsYourGoalAdapter!!.changeSelectionByID(planData.planId!!)
        }else{
            whatsYourGoalAdapter!!.changeSelection(0)
        }

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
