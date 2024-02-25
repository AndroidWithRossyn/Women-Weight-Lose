package com.loseweight

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.WorkoutListAdapter
import com.loseweight.databinding.ActivityExerciseListBinding
import com.loseweight.dialog.ExerciseDetailDialogFragment
import com.loseweight.interfaces.AdsCallback
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.CommonConstantAd
import com.loseweight.utils.Constant
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import kotlin.random.Random
import kotlin.random.nextInt

class ExercisesListActivity : BaseActivity() {

    var binding: ActivityExerciseListBinding? = null
    var workoutListAdapter: WorkoutListAdapter? = null
    var workoutPlanData: HomePlanTableClass? = null
    var exerciseDetailDialog: ExerciseDetailDialogFragment? = null
    var dayId: String? = null
    var from:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_list)

        if(Utils.isPurchased(this).not()) {
            showUnlockTrainingDialog(this)
        }
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
                }

                if (intent.extras!!.containsKey("from")) {
                    from = intent.getStringExtra("from")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()
        workoutListAdapter = WorkoutListAdapter(this)
        binding!!.rvWorkOuts.layoutManager = LinearLayoutManager(this)
        binding!!.rvWorkOuts.setAdapter(workoutListAdapter)

        workoutListAdapter!!.setEventListener(object : WorkoutListAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                exerciseDetailDialog = ExerciseDetailDialogFragment.newInstance(
                    workoutPlanData!!,
                    workoutListAdapter!!.data,
                    this@ExercisesListActivity
                )
                exerciseDetailDialog?.show(position, getSupportFragmentManager(), false)
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
                binding!!.llTopTitle.visibility =
                    View.GONE //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })
        binding!!.imgInstructionArrow.animate().rotation(180f).start()

        fillData()
        getExerciseData()
    }

    private fun getExerciseData() {
        if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
            dayId = intent.getStringExtra(Constant.extra_day_id)
            if (dayId != null) {
                workoutListAdapter!!.addAll(dbHelper.getDayExList(dayId!!))
            }
        }else if (from.isNullOrEmpty().not() && from == Constant.FROM_FAST_WORKOUT) {
            val exList = dbHelper.getHomeDetailExList(workoutPlanData!!.planId!!)

            val newList: HashMap<String,HomeExTableClass> = hashMapOf()
            var i = 0
            while (i < (workoutPlanData!!.planMinutes?.toInt() ?: 2)*2)
            {
                val randomIndex: Int = Random.nextInt(exList.size)
                Debug.e("random", randomIndex.toString())
                val item = exList.get(randomIndex)
                if(newList.containsKey(item.dayExId).not()){
                    i++
                    Debug.e("add $i", randomIndex.toString())
                    newList.put(item.dayExId!!,item)
                }
            }
            workoutListAdapter!!.addAll(newList.values.toList() as ArrayList<HomeExTableClass>)
        }else {
            workoutListAdapter!!.addAll(dbHelper.getHomeDetailExList(workoutPlanData!!.planId!!))
        }

        if (workoutListAdapter!!.isAllCompleted()) {
            binding!!.llContinue.visibility = View.GONE
            binding!!.btnStart.visibility = View.GONE
            binding!!.btnDoItAgain.visibility = View.VISIBLE
        } else if (workoutListAdapter!!.isAnyCompleted()) {
            binding!!.llContinue.visibility = View.VISIBLE
            binding!!.btnStart.visibility = View.GONE
            binding!!.btnDoItAgain.visibility = View.GONE
        } else {
            binding!!.llContinue.visibility = View.GONE
            binding!!.btnDoItAgain.visibility = View.GONE
            binding!!.btnStart.visibility = View.VISIBLE
        }

    }


    private fun fillData() {
        if (workoutPlanData != null) {

            binding!!.imgCover.setImageResource(
                Utils.getDrawableId(
                    workoutPlanData!!.planImage,
                    this
                )
            )

            if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
                binding!!.tvTitleText.text = "Day " + intent.getStringExtra("day_name")
                binding!!.tvTitle.text = "Day " + intent.getStringExtra("day_name")
                binding!!.tvShortDes.text = workoutPlanData!!.planName
            } else {
                binding!!.tvTitleText.text = workoutPlanData!!.planName
                binding!!.tvTitle.text = workoutPlanData!!.planName

                if (workoutPlanData!!.shortDes.isNullOrEmpty().not()) {
                    binding!!.tvShortDes.text = workoutPlanData!!.shortDes
                }
            }

            if (workoutPlanData!!.introduction.isNullOrEmpty().not()) {


                if (workoutPlanData!!.planTestDes.isNullOrEmpty().not()) {
                    binding!!.tvAbout.visibility = View.VISIBLE
                 } else {
                     binding!!.tvAbout.visibility = View.GONE

                 }
                binding!!.tvIntroductionDes.text = workoutPlanData!!.introduction

            } else {
                binding!!.llIntroduction.visibility = View.GONE
            }

            /*if (workoutPlanData!!.planType!!.equals(Constant.PlanTypeWorkouts) && workoutPlanData!!.planDays != Constant.PlanDaysYes) {
                binding!!.imgEdit.visibility = View.VISIBLE
            } else {
                binding!!.imgEdit.visibility = View.GONE
            }*/

            binding!!.tvWorkoutTime.text =
                workoutPlanData!!.planMinutes + " " + getString(R.string.mins)

            if (workoutPlanData!!.planWorkouts.equals("0") && workoutPlanData!!.planLvl.isNullOrEmpty().not())
                binding!!.tvTotalWorkout.text = workoutPlanData!!.planLvl
            else
                binding!!.tvTotalWorkout.text =
                    workoutPlanData!!.planWorkouts + " " + getString(R.string.workouts)


        }
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onStartClick() {

            fun startExerciseActivity(){
                val intent = Intent(this@ExercisesListActivity, PerformWorkOutActivity::class.java)
                intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
                intent.putExtra("ExcList", Gson().toJson(workoutListAdapter!!.data))
                startActivityForResult(intent, 7979)
            }

            var adsCallback = object :AdsCallback{
                override fun adLoadingFailed() {
                    startExerciseActivity()

                }

                override fun adClose() {
                    startExerciseActivity()
                }

                override fun startNextScreen() {
                    startExerciseActivity()
                }

            }

            if (Utils.getPref(this@ExercisesListActivity, Constant.START_BTN_COUNT, 1) == 1) {
                if (Utils.getPref(
                        this@ExercisesListActivity,
                        Constant.STATUS_ENABLE_DISABLE,
                        ""
                    ) == Constant.ENABLE
                ) {
                    when (Utils.getPref(
                        this@ExercisesListActivity,
                        Constant.AD_TYPE_FB_GOOGLE,
                        ""
                    )) {
                        Constant.AD_GOOGLE -> {
                            CommonConstantAd.showInterstitialAdsGoogle(
                                this@ExercisesListActivity,
                                adsCallback
                            )
                        }
                        Constant.AD_FACEBOOK -> {
                            CommonConstantAd.showInterstitialAdsFacebook(
                                adsCallback
                            )
                        }
                        else -> {
                            startExerciseActivity()
                        }
                    }
                    Utils.setPref(this@ExercisesListActivity, Constant.START_BTN_COUNT, 0)
                } else {
                    startExerciseActivity()
                }
            } else {
                Utils.setPref(this@ExercisesListActivity, Constant.START_BTN_COUNT, 1)
                startExerciseActivity()
            }


        }

        fun onEditClick() {
            val intent = Intent(this@ExercisesListActivity, EditPlanActivity::class.java)
            intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            intent.putExtra(Constant.extra_day_id, dayId)
            startActivityForResult(intent, 7979)
        }

        fun onRestartClick() {
            if(dayId != null)
            dbHelper.reStartPlanDay(dayId!!)
            else
                workoutPlanData?.planId?.let { dbHelper.restartDayPlan(it) }
            val intent = Intent(this@ExercisesListActivity, PerformWorkOutActivity::class.java)
            intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            intent.putExtra("ExcList", Gson().toJson(workoutListAdapter!!.data))
            startActivityForResult(intent, 7979)
        }

        fun onContinueClick() {
            val intent = Intent(this@ExercisesListActivity, PerformWorkOutActivity::class.java)
            intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            intent.putExtra("ExcList", Gson().toJson(workoutListAdapter!!.data))
            intent.putExtra("currentPos", workoutListAdapter!!.continuePos)
            startActivityForResult(intent, 7979)
        }

        fun onCommonQuestionClick() {
            val intent = Intent(this@ExercisesListActivity, CommonQuestionActivity::class.java)
            startActivity(intent)
        }

        fun onIntroductionClick() {
            if (binding!!.llIntroductionDes.visibility == View.VISIBLE) {
                binding!!.llIntroductionDes.visibility = View.GONE
                binding!!.imgInstructionArrow.animate().rotation(0f).start()
            } else {
                binding!!.llIntroductionDes.visibility = View.VISIBLE
                binding!!.imgInstructionArrow.animate().rotation(180f).start()

            }
        }

        fun onBackClick() {
            finish()
        }

        fun onAboutClick() {
            val i = Intent(this@ExercisesListActivity, AboutActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            startActivity(i)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 7979 && resultCode == Activity.RESULT_OK) {
            fillData()
            getExerciseData()
        }
    }

}
