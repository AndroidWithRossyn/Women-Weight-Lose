package com.loseweight

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.WorkoutProgressIndicatorAdapter
import com.loseweight.databinding.ActivityPerformExerciesBinding
import com.loseweight.databinding.DialogQuiteWorkoutBinding
import com.loseweight.dialog.ExerciseDetailDialogFragment
import com.loseweight.interfaces.AdsCallback
import com.loseweight.interfaces.DialogDismissListener
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.*
import com.loseweight.utils.Constant
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import java.util.*


class PerformWorkOutActivity : BaseActivity() {

    val TAG = PerformWorkOutActivity::class.java.name + Constant.ARROW
    var binding: ActivityPerformExerciesBinding? = null
    var workoutPlanData: HomePlanTableClass? = null
    var exercisesList: ArrayList<HomeExTableClass>? = null
    private lateinit var mySoundUtil: MySoundUtil

    var currentPos = 0
    var currentExe: HomeExTableClass? = null
    var totalExTime = 0L

    private var exStartTime: Long = 0
    private var running = false
    private var currentTime: Long = 0
    private var timeCountDown = 0
    var isPaused = false

    var timer: CountDownTimerWithPause? = null
    private var mWakeLock: PowerManager.WakeLock? = null


    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_perform_exercies)

        initIntentParam()
        loadInterstialAd()
        init()
        initReadyToGo()

        val pm =  getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag")
        mWakeLock!!.acquire()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomePlanTableClass>() {}.type)!!
                }

                if (intent.extras!!.containsKey("ExcList")) {
                    val str = intent.getStringExtra("ExcList")
                    exercisesList = Gson().fromJson(
                        str,
                        object : TypeToken<ArrayList<HomeExTableClass>>() {}.type
                    )!!
                    binding!!.progressBarTop.max = exercisesList!!.size
                    binding!!.progressBarTop.progress = 0
                }

                if (intent.extras!!.containsKey("currentPos")) {
                    currentPos = intent.getIntExtra("currentPos", 0)
                    if (exercisesList.isNullOrEmpty().not()) {
                        currentExe = exercisesList!!.get(currentPos)
                    }
                } else {
                    if (exercisesList.isNullOrEmpty().not()) {
                        currentExe = exercisesList!!.get(0)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()
        mySoundUtil = MySoundUtil(this)

        /* workoutProgressIndicatorAdapter = WorkoutProgressIndicatorAdapter(this)
         binding!!.rcyWorkoutStatus.layoutManager =
             LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
         binding!!.rcyWorkoutStatus.setAdapter(workoutProgressIndicatorAdapter)
         workoutProgressIndicatorAdapter!!.setTotalExercise(exercisesList!!.size)

         initMusic(true)*/

        setMuteButton()
    }

    private fun loadWorkoutImage() {

        binding!!.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(currentExe!!.exPath!!)
                ?.let { Utils.getAssetItems(this, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(this)
                //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(this).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                binding!!.viewFlipper.addView(imgview)
            }
        }

        binding!!.viewFlipper.isAutoStart = true
        binding!!.viewFlipper.setFlipInterval(resources.getInteger(R.integer.viewfliper_animation))
        binding!!.viewFlipper.startFlipping()
    }

    private fun initReadyToGo() {

        loadWorkoutImage()
        binding!!.llReadyToGo.visibility = View.VISIBLE
        binding!!.tvTitleReadyToGo.visibility = View.VISIBLE
        binding!!.llAfterStartWithTime.visibility = View.GONE
        binding!!.llAfterStartWithSteps.visibility = View.GONE
        binding!!.tvExerciesName.text = currentExe!!.exName

        countDownReadyToGo()

        val readyToGoText = "Ready to go start with ${currentExe!!.exName}"
        if (Utils.getPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true))
            MyApplication.speechText(this, readyToGoText)
    }

    private fun countDownReadyToGo() {

        var timeCountDown = 0

        val readyToGoTime =
            Utils.getPref(this, Constant.PREF_READY_TO_GO_TIME, Constant.DEFAULT_READY_TO_GO_TIME)
        binding!!.progressBarReadyToGo.progressMax = readyToGoTime.toFloat()-1

        timer = object : CountDownTimerWithPause(readyToGoTime * 1000L, 1000, true) {
            override fun onFinish() {
                binding!!.tvCountDownReadyToGO.text = "0"
                binding!!.progressBarReadyToGo.progress = readyToGoTime.toFloat()
                exStartTime = System.currentTimeMillis()
                startPerformExercise(false)
            }

            override fun onTick(millisUntilFinished: Long) {
                timeCountDown++
                if (readyToGoTime - timeCountDown >= 0) {
                    binding!!.tvCountDownReadyToGO.text = (readyToGoTime - timeCountDown).toString()
                    binding!!.progressBarReadyToGo.progress = timeCountDown.toFloat()

                    if (timeCountDown == readyToGoTime.toInt() / 2) {

                        val readyToGoText = "Please do that on a mat"
                        if (Utils.getPref(
                                this@PerformWorkOutActivity,
                                Constant.PREF_IS_INSTRUCTION_SOUND_ON,
                                true
                            )
                        )
                            MyApplication.speechText(this@PerformWorkOutActivity, readyToGoText)

                    } else if ((readyToGoTime - timeCountDown) < 4) {
                        if (Utils.getPref(
                                this@PerformWorkOutActivity,
                                Constant.PREF_IS_INSTRUCTION_SOUND_ON,
                                true
                            )
                        )
                            MyApplication.speechText(
                                this@PerformWorkOutActivity,
                                (readyToGoTime - timeCountDown).toString()
                            )
                    }
                } else {
                    timer!!.onFinish()
                    timer!!.cancel()
                }
            }

        }


        Handler().postDelayed(Runnable {
            timer!!.start()
        }, 1000)

    }

    fun startPerformExercise(isNeedDelay: Boolean) {

        // workoutProgressIndicatorAdapter!!.setCompletedExercise(currentPos)
        binding!!.tvTitleReadyToGo.visibility = View.GONE
        if (currentPos == 0) {

            binding!!.llPrev.isClickable = false
            binding!!.tvPrev.setTextColor(ContextCompat.getColor(this, R.color.col_999))
            binding!!.imgPrev.imageTintList = ContextCompat.getColorStateList(this, R.color.col_999)
        } else {
            binding!!.llPrev.isClickable = true
            binding!!.tvPrev.setTextColor(ContextCompat.getColor(this, R.color.col_666))
            binding!!.imgPrev.imageTintList = ContextCompat.getColorStateList(this, R.color.col_666)
        }

        /*if (currentPos >= exercisesList?.size?.minus(1) ?: 0) {

            binding!!.llSkip.isClickable = false
            binding!!.tvSkip.setTextColor(ContextCompat.getColor(this,R.color.col_999))
            binding!!.imgSkip.imageTintList = ContextCompat.getColorStateList(this,R.color.col_999)
        } else {
            binding!!.llSkip.isClickable = true
            binding!!.tvSkip.setTextColor(ContextCompat.getColor(this,R.color.col_666))
            binding!!.imgSkip.imageTintList = ContextCompat.getColorStateList(this,R.color.col_666)
        }*/

        if (currentExe!!.exUnit.equals(Constant.workout_type_step)) {

            binding!!.tvExerciesName.text = currentExe!!.exName
            binding!!.tvTotalStep.text = currentExe!!.exTime!!
            binding!!.llReadyToGo.visibility = View.GONE
            binding!!.llAfterStartWithTime.visibility = View.GONE
            binding!!.llAfterStartWithSteps.visibility = View.VISIBLE

        } else {
            binding!!.tvExerciesName.text = currentExe!!.exName
            binding!!.llReadyToGo.visibility = View.GONE
            binding!!.llAfterStartWithTime.visibility = View.VISIBLE
            binding!!.llAfterStartWithSteps.visibility = View.GONE

            binding!!.progressBarWorkOut.max = currentExe!!.exTime!!.toInt()
            binding!!.progressBarWorkOut.progress = 0
            //binding!!.tvCompletedSec.text = "${currentExe!!.exTime!!}\""
            binding!!.tvSecond.text =
                Utils.secToString(currentExe!!.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }

        if (isNeedDelay) {
            val scaleAnimation: Animation = ScaleAnimation(
                1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            scaleAnimation!!.setDuration(1000)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding!!.tvAnimation.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
            timer = object : CountDownTimerWithPause(4000, 1000, true) {
                override fun onFinish() {
                    timer?.cancel()
                    timer = null
                    binding!!.tvAnimation.visibility = View.GONE
                    if (currentExe!!.exUnit.equals(Constant.workout_type_step)) {
                        startExerciseWithStep()
                    } else {
                        startExerciseWithTime()
                    }
                    this@PerformWorkOutActivity.start()
                }

                override fun onTick(millisUntilFinished: Long) {
                    if ((millisUntilFinished / 1000) > 0) {
                        if (Utils.getPref(
                                this@PerformWorkOutActivity,
                                Constant.PREF_IS_INSTRUCTION_SOUND_ON,
                                true
                            )
                        )
                            MyApplication.speechText(
                                this@PerformWorkOutActivity,
                                (millisUntilFinished / 1000).toString()
                            )
                        Debug.e("321 Anim", (millisUntilFinished / 1000).toString())
                        binding!!.tvAnimation!!.setText((millisUntilFinished / 1000).toString())
                        binding!!.tvAnimation.visibility = View.VISIBLE
                        binding!!.tvAnimation!!.startAnimation(scaleAnimation)
                    }
                }

            }
            timer!!.start()
        } else {
            start()
            if (currentExe!!.exUnit.equals(Constant.workout_type_step)) {
                startExerciseWithStep()
            } else {
                startExerciseWithTime()
            }
        }


    }

    private fun start() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 1000)
        exStartTime = System.currentTimeMillis()
        running = true
    }

    private fun startExerciseWithStep() {
        if (timer != null) {
            timer!!.cancel()
        }
        if (Utils.getPref(this, Constant.PREF_IS_COACH_SOUND_ON, true))
        mySoundUtil.playSound(0)

    }

    private fun startExerciseWithTime() {
        if (timer != null) {
            timer!!.cancel()
        }
        if (Utils.getPref(this, Constant.PREF_IS_COACH_SOUND_ON, true))
        mySoundUtil.playSound(0)

        try {
            countExercise()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val excTime = currentExe!!.exTime!!.toInt()
        val readyToGoText = "Start $excTime seconds ${currentExe!!.exName}"

        if (Utils.getPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true))
            MyApplication.speechText(this, readyToGoText)
    }

    private fun countExercise() {

        var timeCountDown = 0

        val exerciseTime = currentExe!!.exTime!!.toInt()
        val halfTime = exerciseTime / 2

//        binding!!.progressBarReadyToGo.progressMax = currentExe!!.exTime!!.toFloat()-1
        timer =
            object : CountDownTimerWithPause(currentExe!!.exTime!!.toInt() * 1000L, 1000, true) {
                override fun onFinish() {
                    binding!!.tvSecond.text = Utils.secToString(0, Constant.WORKOUT_TIME_FORMAT)
                    binding!!.progressBarWorkOut.progress = exerciseTime
                    onWorkoutTimeOver()
                }

                override fun onTick(millisUntilFinished: Long) {
                    timeCountDown++
                    if ((exerciseTime - timeCountDown) >= 0) {
                        binding!!.tvSecond.text = Utils.secToString(
                            (exerciseTime - timeCountDown),
                            Constant.WORKOUT_TIME_FORMAT
                        )
                        binding!!.progressBarWorkOut.progress = timeCountDown

                        if (Utils.getPref(
                                this@PerformWorkOutActivity,
                                Constant.PREF_IS_INSTRUCTION_SOUND_ON,
                                true
                            )
                        ) {
                            if (timeCountDown == halfTime) {
                                MyApplication.speechText(this@PerformWorkOutActivity, "Half time")
                            } else if ((exerciseTime - timeCountDown) < 4) {
                                MyApplication.speechText(
                                    this@PerformWorkOutActivity,
                                    (exerciseTime - timeCountDown).toString()
                                )
                            }
                        }
                    } else {
                        timer?.onFinish()
                        timer?.cancel()
                    }
                }

            }

        Handler().postDelayed(Runnable {
            if (timer != null)
                timer!!.start()
        }, 1000)


    }

    private fun onWorkoutTimeOver() {
        stopTimer()

        if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
            dbHelper.updateCompleteExByDayExId(currentExe!!.dayExId!!)
        } else {
            DataHelper(this).updateCompleteHomeExByDayExId(currentExe!!.dayExId!!)
        }
        if (currentPos == exercisesList!!.lastIndex) {
            // Go to Complete Screen
            if (Utils.getPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true))
                MyApplication.speechText(this, "Congratulation")
            goToCompleteScreen()
        } else {
            goToRestScreen()
        }
    }

    private fun goToRestScreen() {
        if (Utils.getPref(this, Constant.PREF_IS_COACH_SOUND_ON, true))
        mySoundUtil.playSound(mySoundUtil.SOUND_DING)
        val i = Intent(this, RestActivity::class.java)
        i.putExtra("nextEx", Gson().toJson(exercisesList!!.get(currentPos + 1)))
        i.putExtra("nextPos", currentPos + 2)
        i.putExtra("totalEx", exercisesList!!.size)
        startActivityForResult(i, 7029)
    }

    private fun goToCompleteScreen() {

        fun startCompleteActivity()
        {
            val i = Intent(this@PerformWorkOutActivity, CompletedActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            i.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
            i.putExtra("ExcList", Gson().toJson(exercisesList))
            i.putExtra("duration", totalExTime)
            startActivity(i)
            finish()
        }

        val adsCallback = object :AdsCallback{
            override fun adLoadingFailed() {
                startCompleteActivity()
            }

            override fun adClose() {
                startCompleteActivity()
            }

            override fun startNextScreen() {
                startCompleteActivity()
            }

        }

        if (Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, "") == Constant.ENABLE &&
            Utils.getPref(this, Constant.AD_TYPE_FB_GOOGLE, "") == Constant.AD_GOOGLE
        ) {

            CommonConstantAd.showInterstitialAdsGoogle(this, adsCallback)
        } else if (Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, "") == Constant.ENABLE &&
            Utils.getPref(this, Constant.AD_TYPE_FB_GOOGLE, "") == Constant.AD_FACEBOOK
        ) {
            CommonConstantAd.showInterstitialAdsFacebook(adsCallback)
        } else {
            startCompleteActivity()
        }



    }

    override fun onResume() {
        super.onResume()
        resumeTimer()
    }


    inner class ClickHandler {

        fun onWorkOutInfoClick() {
            pauseTimer()
            showExerciseDetailDialog()
        }

        fun onVideoClick() {
            pauseTimer()
            showExerciseDetailDialog()
        }

        fun onCommonQuestionClick() {
            val i = Intent(this@PerformWorkOutActivity, CommonQuestionActivity::class.java)
            startActivity(i)
        }

        fun onSoundClick() {
            pauseTimer()
            showSoundOptionDialog(this@PerformWorkOutActivity, object : DialogDismissListener {
                override fun onDialogDismiss() {
                    setMuteButton()
                    resumeTimer()
                }
            })
        }

        fun onReadyToGoClick() {
            pauseTimer()
            showExerciseDetailDialog()
        }


        fun onSkipReadyToGoClick() {
            startPerformExercise(false)
        }

        fun onBackClick() {
            pauseTimer()
            showQuitDialog()
        }

        fun onNextExerciseClick() {
            stopTimer()
            onWorkoutTimeOver()
        }

        fun onPrevExerciseClick() {
            stopTimer()
            if (currentPos >= 1) {
                val i = Intent(this@PerformWorkOutActivity, RestActivity::class.java)
                i.putExtra("nextEx", Gson().toJson(exercisesList!!.get(currentPos - 1)))
                i.putExtra("nextPos", currentPos - 1)
                i.putExtra("totalEx", exercisesList!!.size)

                currentPos -= 2

                startActivityForResult(i, 7029)
            } else {
                totalExTime = 0
                currentPos = 0
                exStartTime = System.currentTimeMillis()
                currentTime = 0
                running = false
                initReadyToGo()
            }
        }

    }

    private fun setMuteButton() {
        if (Utils.getPref(this, Constant.PREF_IS_SOUND_MUTE, false)) {
            binding!!.imgSound.setImageResource(R.drawable.wp_ic_mute)
        } else {
            binding!!.imgSound.setImageResource(R.drawable.wp_ic_sound)
        }
    }

    private fun showExerciseDetailDialog() {
        val exerciseDetailDialog = exercisesList?.let {
            ExerciseDetailDialogFragment.newInstance(
                workoutPlanData!!,
                it, this@PerformWorkOutActivity
            )
        }

        exerciseDetailDialog?.setOnEventListener(object :
            ExerciseDetailDialogFragment.DialogDismissListener {
            override fun onDismissListener(exerciseList: MutableList<HomeExTableClass>) {
                resumeTimer()
            }
        })

        exerciseDetailDialog?.show(currentPos, getSupportFragmentManager(), true)
    }

    /*private fun playMusic() {
        if (currMusic != null) {
            if (MyApplication.musicUtil == null || MyApplication.musicUtil!!.isPlaying.not()) {
                binding!!.imgPlayMusic.visibility = View.GONE
                binding!!.imgPauseMusic.visibility = View.VISIBLE
                binding!!.imgMusic.setImageResource(R.drawable.ic_music)
                MyApplication.playMusic(currMusic!!, this@PerformWorkOutActivity)
                Utils.setPref(this@PerformWorkOutActivity, Constant.PREF_IS_MUSIC_MUTE, false)
            } else {
                binding!!.imgPlayMusic.visibility = View.VISIBLE
                binding!!.imgPauseMusic.visibility = View.GONE
                binding!!.imgMusic.setImageResource(R.drawable.ic_music_off)
                Utils.setPref(this@PerformWorkOutActivity, Constant.PREF_IS_MUSIC_MUTE, true)
                MyApplication.stopMusic()
            }
        }
    }

    fun setPlayPauseView() {
        if (MyApplication.musicUtil == null || MyApplication.musicUtil!!.isPlaying.not()) {
            binding!!.imgPlayMusic.visibility = View.VISIBLE
            binding!!.imgPauseMusic.visibility = View.GONE
            binding!!.imgMusic.setImageResource(R.drawable.ic_music_off)
        } else {
            binding!!.imgPlayMusic.visibility = View.GONE
            binding!!.imgPauseMusic.visibility = View.VISIBLE
            binding!!.imgMusic.setImageResource(R.drawable.ic_music)
        }
    }*/

    override fun onPause() {
        super.onPause()
        pauseTimer()
    }

    override fun onBackPressed() {
        pauseTimer()
        showQuitDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
            Debug.e(TAG, "OnTimer cancel")
        }
    }

    fun resumeTimer() {
        if (timer != null && timer!!.isPaused) {
            timer!!.resume()
            Debug.e(TAG, "OnTimer resume")
        }
        if (running.not()) {
            running = true
            exStartTime = System.currentTimeMillis()
            Debug.e("resumeTimer exStartTime", exStartTime.toString())
        }

    }

    fun pauseTimer() {
        if (running) {
            running = false
            currentTime = System.currentTimeMillis() - exStartTime
            totalExTime += currentTime / 1000
            Debug.e("pauseTimer currentTime", currentTime.toString())
            Debug.e("pauseTimer totalExTime", totalExTime.toString())
        }

        if (timer != null && timer!!.isRunning) {
            timer!!.pause()
            Debug.e(TAG, "OnTimer pause")
        }
    }

    lateinit var quiteDialog: Dialog
    fun showQuitDialog() {
        if (Utils.getPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true))
            MyApplication.speechText(this, getString(R.string.quite_exercise_msg))
        quiteDialog = Dialog(getActivity())
        quiteDialog.setCancelable(false)
        quiteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        quiteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var dialogQuiteWorkoutBinding =
            DataBindingUtil.inflate<DialogQuiteWorkoutBinding>(
                getLayoutInflater(),
                R.layout.dialog_quite_workout, null, false
            )

        quiteDialog.setContentView(dialogQuiteWorkoutBinding.root)

        dialogQuiteWorkoutBinding!!.imgClose.setOnClickListener {
            resumeTimer()
            quiteDialog.dismiss()
        }

        dialogQuiteWorkoutBinding!!.btnContinue.setOnClickListener {
            resumeTimer()
            quiteDialog.dismiss()
        }

        dialogQuiteWorkoutBinding!!.btnQuit.setOnClickListener {

            var adsCallBack = object : AdsCallback {

                override fun adLoadingFailed() {
                    saveData()
                }

                override fun adClose() {
                    saveData()
                }

                override fun startNextScreen() {
                    saveData()
                }

            }

            if (Utils.getPref(this, Constant.EXIT_BTN_COUNT, 1) == 2) {
                if (Utils.getPref(this, Constant.AD_TYPE_FB_GOOGLE, "") == Constant.AD_GOOGLE &&
                    Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, "") == Constant.ENABLE
                ) {
                    CommonConstantAd.showInterstitialAdsGoogle(this, adsCallBack)
                } else if (Utils.getPref(
                        this,
                        Constant.AD_TYPE_FB_GOOGLE,
                        ""
                    ) == Constant.AD_FACEBOOK &&
                    Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, "") == Constant.ENABLE
                ) {
                    CommonConstantAd.showInterstitialAdsFacebook(adsCallBack)
                } else {
                    saveData()
                }
                Utils.setPref(this, Constant.EXIT_BTN_COUNT, 1)
            } else {
                Utils.setPref(this, Constant.EXIT_BTN_COUNT, 2)
                saveData()
            }

            quiteDialog.dismiss()

        }

        dialogQuiteWorkoutBinding!!.tvComeback.setOnClickListener {
            quiteDialog.dismiss()
            finish()
        }

        quiteDialog.show()
    }


    private fun saveData() {

        try {
            val calValue = Constant.SEC_DURATION_CAL * totalExTime

            dbHelper.addHistory(
                exercisesList!![0].planId!!,
                dbHelper.getPlanNameByPlanId(exercisesList!![0].planId!!),
                Utils.parseTime(Date().time, Constant.DATE_TIME_24_FORMAT),
                totalExTime.toString(),
                calValue.toString(),
                exercisesList!!.size.toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString(),
                "0",
                dbHelper.getPlanDayNameByDayId(exercisesList!![0].dayId!!),
                exercisesList!![0].dayId!!
            )

            //LocalDB.setLastUnCompletedExPos(this, arrDayExTableClass[0].planId.toInt(), arrDayExTableClass[0].dayId, viewPagerWorkout.currentItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setResult(RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 7029 && resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra("restTime")) {
                totalExTime += data.getLongExtra("restTime", 0)
                Debug.e("onActivityResult totalExTime", totalExTime.toString())
            }

            currentPos++
            binding!!.progressBarTop.progress = currentPos
            currentExe = exercisesList!!.get(currentPos)
            loadWorkoutImage()
            if (data!!.hasExtra("isRestSkip")) {
                startPerformExercise(data!!.getBooleanExtra("isRestSkip", false))
            } else {
                startPerformExercise(false)
            }

        }
    }
}
