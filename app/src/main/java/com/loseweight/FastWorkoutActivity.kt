package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.loseweight.adapter.RandomWorkoutAdapter
import com.loseweight.adapter.TrainingGoalAdapter
import com.loseweight.databinding.ActivityFastWorkOutBinding
import com.loseweight.utils.Constant

class FastWorkoutActivity : BaseActivity() {

    var binding: ActivityFastWorkOutBinding? = null
    var trainingGoalAdapter: TrainingGoalAdapter? = null
    var randomWorkoutAdapter: RandomWorkoutAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fast_work_out)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            /*if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomePlanTableClass>() {}.type)!!
                }
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()
        trainingGoalAdapter = TrainingGoalAdapter(this)
        binding!!.rvTrainingGoal.layoutManager = GridLayoutManager(this,2)
        binding!!.rvTrainingGoal.setAdapter(trainingGoalAdapter)

        trainingGoalAdapter!!.setEventListener(object : TrainingGoalAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = trainingGoalAdapter!!.getItem(position)
                val i = Intent(this@FastWorkoutActivity,FastWorkOutDetailActivity::class.java)
                i.putExtra("workoutPlanData",Gson().toJson(item))
                startActivity(i)
            }

        })

        randomWorkoutAdapter = RandomWorkoutAdapter(this)
        binding!!.rvRandomWorkout.layoutManager = GridLayoutManager(this,2)
        binding!!.rvRandomWorkout.setAdapter(randomWorkoutAdapter)
        randomWorkoutAdapter!!.setEventListener(object : RandomWorkoutAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = randomWorkoutAdapter!!.getItem(position)
                val i = Intent(this@FastWorkoutActivity,FastWorkOutDetailActivity::class.java)
                i.putExtra("workoutPlanData",Gson().toJson(item))
                startActivity(i)
            }

        })

        var isShow = true
        var scrollRange = -1
        binding!!.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding!!.llTopTitle.visibility = View.VISIBLE
                binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                isShow = true
            } else if (isShow) {
                binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.gray_light_), android.graphics.PorterDuff.Mode.SRC_IN);
                binding!!.llTopTitle.visibility = View.GONE //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })

        fillData()

    }


    private fun fillData() {

        randomWorkoutAdapter!!.addAll(dbHelper.getHomePlanList(Constant.PlanTypeFastWorkoutRandom))
        trainingGoalAdapter!!.addAll(dbHelper.getHomePlanList(Constant.PlanTypeFastWorkoutTrainingGoal))

    }

    override fun onResume() {
        super.onResume()
    }

    inner class ClickHandler {

        fun onFatBurningClick() {
            val item = dbHelper.getHomePlanList(Constant.PlanTypeFastWorkoutFatBurning)
            val i = Intent(this@FastWorkoutActivity,FastWorkOutDetailActivity::class.java)
            i.putExtra("workoutPlanData",Gson().toJson(item[0]))
            startActivity(i)
        }

        fun onBackClick() {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}
