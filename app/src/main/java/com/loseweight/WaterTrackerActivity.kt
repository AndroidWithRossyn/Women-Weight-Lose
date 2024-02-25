package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.loseweight.databinding.ActivityWaterTrackerBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import java.util.*


class WaterTrackerActivity : BaseActivity() {
    val TAG = WaterTrackerActivity::class.java.name + Constant.ARROW

    var binding: ActivityWaterTrackerBinding? = null
    var count = 0
    var maxCup = 8
    private var waterProgress = 1
    var isTimerRested = false

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 50) {
                if (waterProgress >= count) {
                    Log.d("demo", count.toString() + "")
                    if(waterProgress > 100) {
                        var lastProgress = binding!!.tvPercentage.text.toString().removeSuffix("%").toInt()
                        binding!!.tvPercentage.text = (lastProgress +1).toString()+"%"
                    }
                    else
                        binding!!.tvPercentage.text = "$count%"
                    if ((count.toFloat() / 100) <= 1f) {
                        binding!!.multiWaveHeader.setProgress(count.toFloat() / 100)
                        Debug.e("Progress", (count.toFloat() / 100).toString())
                    } else {
                        count = waterProgress + 1
                        binding!!.tvPercentage.text = "$waterProgress%"
                        if (isTimerRested.not())
                            resetTimer()
                    }
                } else {
                    if (isTimerRested.not())
                        resetTimer()
                }
                count++
            }
        }
    }

    private var timerTask: TimerTask? = object : TimerTask() {
        override fun run() {
            handler.sendEmptyMessage(100)
        }
    }

    private var timer: Timer? = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_water_tracker)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        init()
    }


    private fun init() {


        binding!!.imgBack.setOnClickListener {
            finish()
        }

        binding!!.multiWaveHeader.velocity = 9f
        binding!!.multiWaveHeader.start()


        doing()
    }

    fun doing() {

        val lastDate = Utils.getPref(this, Constant.PREF_WATER_TRACKER_DATE, "")
        val currDate = Utils.parseTime(Date().time, "dd-MM-yyyy")
        var currentCup = Utils.getPref(this, Constant.PREF_WATER_TRACKER_GLASS, 0) + 1
        if (lastDate.equals(currDate).not()) {
            currentCup = 1
        }
        count = ((currentCup - 1) * 100) / 8
        if ((count.toFloat() / 100) <= 0.9f) {
            binding!!.tvPercentage.text = "$count%"
            binding!!.multiWaveHeader.setProgress(count.toFloat() / 100)
            Debug.e("Progress", (count.toFloat() / 100).toString())
        } else {
            binding!!.tvPercentage.text = "$count%"
            count = 90
            binding!!.multiWaveHeader.setProgress(count.toFloat() / 100)
        }

        waterProgress = (currentCup * 100) / 8

        binding!!.tvCups.text = "$currentCup / $maxCup"
        Utils.setPref(this, Constant.PREF_WATER_TRACKER_GLASS, currentCup)

        Handler(Looper.getMainLooper()).postDelayed({
            timerTask = object : TimerTask() {
                override fun run() {
                    handler.sendEmptyMessage(50)
                }
            }
            timer = Timer()
            timer!!.schedule(timerTask, 50, 50)
        }, 500)

    }

    private fun resetTimer() {
        isTimerRested = true
        if (timerTask != null) timerTask!!.cancel()
        timerTask = null
        if (timer != null) timer!!.cancel()
        timer = null

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, WellDoneActivity::class.java)
            startActivity(i)
            finish()
        }, 500)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
