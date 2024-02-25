package com.loseweight

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.WorkoutProgressIndicatorAdapter
import com.loseweight.databinding.ActivityRestBinding
import com.loseweight.dialog.ExerciseDetailDialogFragment
import com.loseweight.objects.HomeExTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.CountDownTimerWithPause
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import java.util.*


class RestActivity : BaseActivity() {

    var binding: ActivityRestBinding? = null
    var timer: CountDownTimerWithPause? = null

    var nextPos = 0
    var totalEx = 0

    var timeCountDown = 0L
    var restTime = 20L // in second
    var nextExercise: HomeExTableClass? = null
    var workoutProgressIndicatorAdapter: WorkoutProgressIndicatorAdapter? = null
    private var mWakeLock: PowerManager.WakeLock? = null

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rest)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        init()

        val pm =  getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag")
        mWakeLock!!.acquire()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("nextEx")) {
                    val str = intent.getStringExtra("nextEx")
                    nextExercise = Gson().fromJson(str, object :
                        TypeToken<HomeExTableClass>() {}.type)!!
                }
                if (intent.extras!!.containsKey("nextPos")) {
                    nextPos = intent.getIntExtra("nextPos", 2)
                }
                if (intent.extras!!.containsKey("totalEx")) {
                    totalEx = intent.getIntExtra("totalEx", 0)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()

        workoutProgressIndicatorAdapter = WorkoutProgressIndicatorAdapter(this)
        binding!!.rcyWorkoutStatus.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding!!.rcyWorkoutStatus.setAdapter(workoutProgressIndicatorAdapter)
        workoutProgressIndicatorAdapter!!.setTotalExercise(totalEx)
        workoutProgressIndicatorAdapter!!.setCompletedExercise(nextPos-1)

        restTime = Utils.getPref(this, Constant.PREF_REST_TIME,Constant.DEFAULT_REST_TIME)

        binding!!.tvName.text = nextExercise!!.exName
        binding!!.tvTotalEx.text = "$nextPos / $totalEx"
        if (nextExercise!!.exUnit.equals(Constant.workout_type_step)) {
            binding!!.tvTime.text = "X ${nextExercise!!.exTime}"
        } else {
            binding!!.tvTime.text = Utils.secToString(nextExercise!!.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }



        binding!!.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(nextExercise!!.exPath!!)
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

        if (!nextExercise!!.exUnit.equals(Constant.workout_type_step)) {
            MyApplication.speechText(
                this,
                "Take a rest"
            )
            Handler().postDelayed(Runnable {
                MyApplication.speechText(
                    this,
                    "Next ${nextExercise!!.exTime} seconds ${nextExercise!!.exName}"
                )
            }, 1000)

        } else {
            MyApplication.speechText(
                this,
                "Take a rest"
            )
            Handler().postDelayed(Runnable {
                MyApplication.speechText(
                    this,
                    "Next ${nextExercise!!.exTime} times ${nextExercise!!.exName}"
                )
            }, 1000)

        }
        countDownRest()

    }

    private fun countDownRest() {
//        binding!!.tvSecond.text = Utils.secToString(restTime.toInt(), "mm:ss")
//        binding!!.progressBarReadyToGo.progressMax = (restTime-1).toFloat()
        binding!!.progressBarReadyToGo.progressMax = (restTime.toFloat()+binding!!.progressBarReadyToGo.progress)-1
        Debug.e("","restTime===>>>  ${restTime.toFloat()+binding!!.progressBarReadyToGo.progress}")
        timer = object : CountDownTimerWithPause(restTime * 1000L, 1000, true) {
            override fun onFinish() {
                val i = Intent()
                i.putExtra("restTime",timeCountDown)
                setResult(Activity.RESULT_OK,i)

                timer!!.cancel()
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                timeCountDown++
                restTime = (millisUntilFinished/1000)
                binding!!.tvCountDownReadyToGO.text = Utils.secToString(((millisUntilFinished / 1000)).toInt(), "mm:ss")
                Debug.e("TAG", "onTick:::::time "+Utils.secToString(((millisUntilFinished / 1000)).toInt(), "mm:ss")+"    "+
                        millisUntilFinished+"    "+millisUntilFinished/1000)
                if (((millisUntilFinished / 1000)) < 4) {
                    //Debug.e("Rest time = " ,""+((millisUntilFinished/1000) - timeCountDown))
                    MyApplication.speechText(
                        this@RestActivity,
                        ((millisUntilFinished / 1000)).toString()
                    )
                }

                binding!!.progressBarReadyToGo.setProgressWithAnimation(timeCountDown.toFloat())
            }

        }

        Handler().postDelayed(Runnable {
            timer!!.start()
        }, 100)

    }


    inner class ClickHandler {

        fun onSkipClick() {
            val i = Intent()
            i.putExtra("restTime",timeCountDown)
            i.putExtra("isRestSkip",true)
            setResult(Activity.RESULT_OK,i)
            finish()
        }

        fun onPlusTimeClick() {
            Debug.e("old seconds = ", restTime.toString())
            Debug.e("time completed seconds = ", timeCountDown.toString())
            restTime += 20
//            binding!!.progressBarReadyToGo.progressMax = restTime.toFloat()
            Debug.e("New seconds = ", restTime.toString())
            timer?.cancel()
            countDownRest()
        }

        fun onExerciseInfoClick() {
            pauseTimer()
            val exList = arrayListOf<HomeExTableClass>()
            nextExercise?.let { exList.add(it) }
            val exerciseDetailDialog = ExerciseDetailDialogFragment.newInstance(null, exList,this@RestActivity)

            exerciseDetailDialog?.setOnEventListener(object : ExerciseDetailDialogFragment.DialogDismissListener{

                override fun onDismissListener(exerciseList: MutableList<HomeExTableClass>) {
                    resumeTimer()
                }
            })

            exerciseDetailDialog?.show(0,getSupportFragmentManager(),true)
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }

    fun resumeTimer() {
        if (timer != null && timer!!.isPaused) {
            timer!!.resume()
        }
    }

    fun pauseTimer() {
        if (timer != null && timer!!.isRunning) {
            timer!!.pause()
        }
    }

    override fun onBackPressed() {
        val i = Intent()
        i.putExtra("restTime",timeCountDown)
        i.putExtra("isRestSkip",true)
        setResult(Activity.RESULT_OK,i)
        super.onBackPressed()

    }
}
