package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.view.numberpicker.NumberPicker
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.databinding.ActivityBmiBinding
import com.loseweight.databinding.ActivityChooseWeightBinding
import com.loseweight.databinding.ActivityChooseYourPlanBinding
import com.loseweight.databinding.ActivityYourPlanBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import kotlin.math.roundToInt


class BMIActivity : BaseActivity() {
    val TAG = BMIActivity::class.java.name + Constant.ARROW

    var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bmi)
        init()
    }


    private fun init() {

        var lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
        val lastFoot = Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0)
        val lastInch = Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F)

        if (lastWeight != 0f && lastFoot != 0 && lastInch.toInt() != 0) {

            val bmiValue = Utils.getBmiCalculation(
                lastWeight,
                lastFoot,
                lastInch.toInt()
            )

            binding!!.tvBMI.text = Utils.truncateUptoTwoDecimal(bmiValue.toString()) //+ "kg/m\u00B2"
            binding!!.tvWeightString.text = Utils.bmiWeightString(bmiValue.toFloat())

        }else{
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        binding!!.btnNext.setOnClickListener {
            val i = Intent(this,YourPlanActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(i)
            finish()
        }

        binding!!.tvSkip.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

}
