package com.loseweight

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.databinding.ActivityCompletedBinding
import com.loseweight.interfaces.DialogDismissListener
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.MySoundUtil
import com.loseweight.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class CompletedActivity : BaseActivity() {

    var binding: ActivityCompletedBinding? = null
    var workoutPlanData: HomePlanTableClass? = null
    var exercisesList: ArrayList<HomeExTableClass>? = null
    var totalDuration = 0L
    private var feelRate = "0"
    var isDataSaved = false

    private lateinit var mySoundUtil: MySoundUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_completed)

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
                if (intent.extras!!.containsKey("duration")) {
                    totalDuration = intent.getLongExtra("duration", 0)
                }
                if (intent.extras!!.containsKey("ExcList")) {
                    val str = intent.getStringExtra("ExcList")
                    exercisesList = Gson().fromJson(
                        str,
                        object : TypeToken<ArrayList<HomeExTableClass>>() {}.type
                    )!!
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()
        mySoundUtil = MySoundUtil(this)
        mySoundUtil.playSound(mySoundUtil.SOUND_CHEER)

        binding!!.editWeight.setOnEditorActionListener { _, actionId, _ ->

            try {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                    var boolKg = weightUnit == Constant.DEF_KG

                    if (binding!!.editWeight.text.toString().isEmpty()) {
                        showToast("Please fill the field")
                    } else if (boolKg && (binding!!.editWeight.text.toString().toFloat() < Constant.MIN_KG || binding!!.editWeight.text.toString()
                            .toFloat() > Constant.MAX_KG)
                    ) {
                        showToast("Please enter valid weight. It should be  between ${Constant.MIN_KG}KG to ${Constant.MAX_KG}KG")
                    } else if (!boolKg &&(binding!!.editWeight.text.toString().toFloat() < Constant.MIN_LB || binding!!.editWeight.text.toString()
                            .toFloat() > Constant.MAX_LB)
                        && (binding!!.editWeight.text.toString()
                            .toFloat() != Constant.MAX_LB.toFloat() || binding!!.editWeight.text.toString()
                            .toFloat() != Constant.MIN_LB.toFloat())
                    ) {
                        showToast("Please enter valid weight. It should be  between ${Constant.MIN_LB}LB to ${Constant.MAX_LB}LB")
                    }else {

                        if (weightUnit != Constant.DEF_KG) {
                            Utils.setPref(
                                this,
                                Constant.PREF_LAST_INPUT_WEIGHT,
                                Utils.lbToKg(binding!!.editWeight.text.toString().toDouble())
                                    .toFloat()
                            )

                        } else {
                            Utils.setPref(
                                this,
                                Constant.PREF_LAST_INPUT_WEIGHT,
                                binding!!.editWeight.text.toString().toFloat()
                            )
                        }
                    }

                    setBmiCalculation()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            false
        }

        fillData()

        setBmiCalculation()
        setWeightValues()
    }


    private fun fillData() {
        if (workoutPlanData != null) {

            binding!!.tvTotalEx.text = exercisesList!!.size.toString()
            binding!!.tvDuration.text =
                Utils.secToString(totalDuration.toInt(), Constant.WORKOUT_TIME_FORMAT)
            binding!!.tvCalorie.text = Utils.getCalorieFromSec(totalDuration).toInt().toString()

            binding!!.tvReminders.text = dbHelper.getRemindersListString()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    /* Todo set bmi calculation */
    private fun setBmiCalculation() {

        try {
            var lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
            val lastFoot = Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0)
            val lastInch = Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F)

            val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)

            if (lastWeight != 0f && lastFoot != 0 && lastInch.toInt() != 0) {

                binding!!.llTapInputHeight.visibility = View.GONE
                binding!!.clBMIGraphView.visibility = View.VISIBLE
                binding!!.imgEditTopBMI.visibility = View.GONE
                binding!!.imgArrowUp.visibility = View.VISIBLE


                val bmiValue = Utils.getBmiCalculation(
                    lastWeight,
                    lastFoot,
                    lastInch.toInt()
                )

                val bmiVal = Utils.calculationForBmiGraph(bmiValue.toFloat())

                val param = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    bmiVal
                )

                binding!!.txtBmiGrade.text = Utils.truncateUptoTwoDecimal(bmiValue.toString())
                binding!!.tvBMI.text =
                    Utils.truncateUptoTwoDecimal(bmiValue.toString()) + "kg/m\u00B2"
                binding!!.tvWeightString.text = Utils.bmiWeightString(bmiValue.toFloat())
                binding!!.tvWeightString.setTextColor(
                    ColorStateList.valueOf(
                        Utils.bmiWeightTextColor(
                            this,
                            bmiValue.toFloat()
                        )
                    )
                )
                binding!!.blankView1.layoutParams = param

            } else {
                binding!!.llTapInputHeight.visibility = View.VISIBLE
                binding!!.clBMIGraphView.visibility = View.GONE
                binding!!.imgEditTopBMI.visibility = View.VISIBLE
                binding!!.imgArrowUp.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /* Todo set weight values */
    private fun setWeightValues() {

        val lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
        val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)

        try {
            if (weightUnit == Constant.DEF_KG && lastWeight != 0f) {
                binding!!.tvKG.isSelected = true
                binding!!.tvLB.isSelected = false
                binding!!.editWeight.setText(Utils.truncateUptoTwoDecimal(lastWeight.toString()))

                binding!!.tvKG.background = resources.getDrawable(R.color.primary, null)
                binding!!.tvLB.background = resources.getDrawable(R.color.gray_light_, null)
            } else if (weightUnit == Constant.DEF_LB && lastWeight != 0f) {
                binding!!.tvKG.isSelected = false
                binding!!.tvLB.isSelected = true
                binding!!.editWeight.setText(
                    Utils.truncateUptoTwoDecimal(
                        Utils.kgToLb(lastWeight.toDouble()).toFloat().toString()
                    )
                )

                binding!!.tvKG.background = resources.getDrawable(R.color.gray_light_, null)
                binding!!.tvLB.background = resources.getDrawable(R.color.primary, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class ClickHandler {

        fun onBackClick() {
            saveData(true)
        }

        fun onAddReminderClick() {
            val i = Intent(this@CompletedActivity, ReminderActivity::class.java)
            startActivityForResult(i, 7983)
        }

        fun onEditBMIClick() {
            showHeightWeightDialog(object : DialogDismissListener {
                override fun onDialogDismiss() {
                    setBmiCalculation()
                    setWeightValues()
                }

            })
        }

        fun onAddHeightClick() {
            showHeightWeightDialog(object : DialogDismissListener {
                override fun onDialogDismiss() {
                    setBmiCalculation()
                    setWeightValues()
                }

            })
        }

        fun onShareClick() {
            val link = "https://play.google.com/store/apps/details?id=$packageName"
            val strSubject = "Share ${resources.getString(R.string.app_name)} with you"
            val strText = " I have finish ${exercisesList!!.size} of ${resources.getString(R.string.app_name)} exercise.\n" +
                        "you should start stretching at home too. You'll get results in no time! \n" +
                        "Please download the app: $link"
            Utils.shareStringLink(this@CompletedActivity, strSubject, strText)
        }

        fun onDoItAgainClick() {
            val intent = Intent(this@CompletedActivity, PerformWorkOutActivity::class.java)
            intent.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            intent.putExtra("ExcList", Gson().toJson(exercisesList!!))
            startActivity(intent)
            finishActivity()
//            finish()
        }

        fun onNextClick() {
            saveData(true)
        }

        fun onKGClick() {
            if (binding!!.tvKG.isSelected.not()) {
                binding!!.tvKG.isSelected = true
                binding!!.tvKG.background = resources.getDrawable(R.color.primary, null)
                binding!!.tvLB.background = resources.getDrawable(R.color.gray_light_, null)

                val lastWeight = binding!!.editWeight.text.toString().toFloat()
                val weightUnit = Constant.DEF_KG

                if (weightUnit != Constant.DEF_LB && lastWeight != 0f) {
                    binding!!.editWeight.setText(
                        Utils.truncateUptoTwoDecimal(
                            Utils.lbToKg(lastWeight.toDouble()).toString()
                        )
                    )
                    Utils.setPref(
                        this@CompletedActivity,
                        Constant.PREF_WEIGHT_UNIT,
                        Constant.DEF_KG
                    )
                }

            }
        }

        fun onLBClick() {
            binding!!.tvKG.isSelected = false
            binding!!.tvLB.isSelected = true
            binding!!.tvLB.background = resources.getDrawable(R.color.primary, null)
            binding!!.tvKG.background = resources.getDrawable(R.color.gray_light_, null)

            val lastWeight = binding!!.editWeight.text.toString().toFloat()
            val weightUnit = Constant.DEF_LB

            if (weightUnit != Constant.DEF_KG && lastWeight != 0f) {
                binding!!.editWeight.setText(
                    Utils.truncateUptoTwoDecimal(
                        Utils.kgToLb(lastWeight.toDouble()).toString()
                    )
                )
                Utils.setPref(this@CompletedActivity, Constant.PREF_WEIGHT_UNIT, Constant.DEF_LB)
            }

        }

        fun onArrowUpClick() {
            if (binding!!.clBMIGraphView.visibility == View.VISIBLE) {
                binding!!.clBMIGraphView.visibility = View.GONE
                binding!!.imgArrowUp.rotation = 180F
                binding!!.imgArrowUp.invalidate()
            } else {
                binding!!.clBMIGraphView.visibility = View.VISIBLE
                binding!!.imgArrowUp.rotation = 0F
                binding!!.imgArrowUp.invalidate()
            }
        }

        fun onSetFeel(str: Int) {
            feelRate = str.toString()
        }

        fun onFeedBackClick() {
            Utils.contactUs(this@CompletedActivity)
        }

    }

    override fun onBackPressed() {
        saveData(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isDataSaved)
            saveData(false)
    }

    // Todo save user history
    private fun saveData(isGoToHistory: Boolean) {

        try {
            dbHelper.addHistory(
                exercisesList!![0].planId!!,
                dbHelper.getPlanNameByPlanId(exercisesList!![0].planId!!),
                Utils.parseTime(Date().time, Constant.DATE_TIME_24_FORMAT),
                totalDuration.toString(),
                Utils.getCalorieFromSec(totalDuration).toString(),
                exercisesList!!.size.toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString(),
                feelRate,
                dbHelper.getPlanDayNameByDayId(exercisesList!![0].dayId!!),
                exercisesList!![0].dayId!!
            )

            if (workoutPlanData!!.planDays!!.equals(Constant.PlanDaysYes))
                dbHelper.updatePlanDayCompleteByDayId(exercisesList!![0].dayId!!)

          // dbHelper.setLastCompletedDay(this, exercisesList[0].planId!!.toInt(), dbHelper.getPlanDayNameByDayId(exercisesList[0].dayId).toInt())

            //dbHelper.setLastUnCompletedExPos(this, exercisesList[0].planId.toInt(), exercisesList[0].dayId, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isDataSaved = true
        if (isGoToHistory) {
            val intent = Intent(
                this@CompletedActivity,
                HistoryActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finishActivity()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 7983 && resultCode == Activity.RESULT_OK) {
            fillData()
        }
    }
}
