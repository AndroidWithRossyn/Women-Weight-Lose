package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.databinding.ActivityChooseYourPlanBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils


class ChooseYourPlanActivity : BaseActivity() {
    val TAG = ChooseYourPlanActivity::class.java.name + Constant.ARROW

    var binding: ActivityChooseYourPlanBinding? = null
    var whatsYourGoalAdapter: ChooseYourPlanAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_your_plan)
        init()
    }


    private fun init() {

        binding!!.mRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        whatsYourGoalAdapter = ChooseYourPlanAdapter(this)
        binding!!.mRecyclerView!!.setAdapter(whatsYourGoalAdapter)
        whatsYourGoalAdapter!!.setEventListener(object : ChooseYourPlanAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                Utils.setPref(this@ChooseYourPlanActivity, Constant.PREF_IS_FIRST_TIME, false)
                val item = whatsYourGoalAdapter!!.getItem(position)
                whatsYourGoalAdapter!!.changeSelection(position)
                Utils.setPref(this@ChooseYourPlanActivity,Constant.PREF_GOAL,Gson().toJson(item))
                val i = Intent(this@ChooseYourPlanActivity,ChooseWeightActivity::class.java)
                startActivity(i)
            }

        })

        binding!!.tvSkip.setOnClickListener {
            Utils.setPref(this@ChooseYourPlanActivity, Constant.PREF_IS_FIRST_TIME, false)
            val item = whatsYourGoalAdapter!!.getItem(0)
            Utils.setPref(this@ChooseYourPlanActivity,Constant.PREF_GOAL,Gson().toJson(item))
            val intent = Intent(getActivity(), HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        addData()
    }

    private fun addData() {
        var list = dbHelper.getHomePlanList(Constant.PlanTypeMainGoals)

        whatsYourGoalAdapter!!.addAll(list)
        whatsYourGoalAdapter!!.changeSelection(0)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
