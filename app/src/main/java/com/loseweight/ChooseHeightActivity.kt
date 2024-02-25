package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.view.numberpicker.NumberPicker
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.databinding.ActivityChooseHeightBinding
import com.loseweight.databinding.ActivityChooseWeightBinding
import com.loseweight.databinding.ActivityChooseYourPlanBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import kotlin.math.roundToInt


class ChooseHeightActivity : BaseActivity() {
    val TAG = ChooseHeightActivity::class.java.name + Constant.ARROW

    var binding: ActivityChooseHeightBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_height)
        init()
    }


    private fun init() {

        val data = arrayOf("cm", " ft+in")

        binding!!.npHeightUnit.wrapSelectorWheel = false
        binding!!.npHeightUnit.minValue = 0
        binding!!.npHeightUnit.maxValue = data.size - 1
        binding!!.npHeightUnit.displayedValues = data
        binding!!.npHeightUnit.value = 0

        binding!!.npFt.minValue = Constant.MIN_FT
        binding!!.npFt.maxValue = Constant.MAX_FT

        binding!!.npIN.minValue = Constant.MIN_IN
        binding!!.npIN.maxValue = Constant.MAX_IN

        binding!!.npCM.minValue = Constant.MIN_CM
        binding!!.npCM.maxValue = Constant.MAX_CM

        // Set scroller enabled
        binding!!.npHeightUnit.isScrollerEnabled = true

        // Set wrap selector wheel
        binding!!.npHeightUnit.wrapSelectorWheel = false

        binding!!.npHeightUnit.setOnValueChangedListener(object :NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {

                if(picker!!.value == 1){

                    binding!!.npCM.visibility = View.GONE
                    binding!!.npFt.visibility = View.VISIBLE
                    binding!!.npIN.visibility = View.VISIBLE
                    binding!!.tvFt.visibility = View.VISIBLE
                    binding!!.tvIn.visibility = View.VISIBLE

                    val inch = Utils.cmToInch(binding!!.npCM.value.toDouble())
                    binding!!.npFt.value = Utils.calcInchToFeet(inch)
                    binding!!.npIN.value = Utils.calcInFromInch(inch).roundToInt()

                } else if(picker.value == 0){

                    binding!!.npCM.visibility = View.VISIBLE
                    binding!!.npFt.visibility = View.GONE
                    binding!!.npIN.visibility = View.GONE
                    binding!!.tvFt.visibility = View.GONE
                    binding!!.tvIn.visibility = View.GONE

                    val inch = Utils.ftInToInch(
                        binding!!.npFt.value,
                        binding!!.npIN.value.toDouble()
                    )

                    binding!!.npCM.value = Utils.inchToCm(inch).roundToInt()
                }

            }
        })

        binding!!.npCM.setSelectedTypeface(Utils.getBold(this))
        binding!!.npCM.typeface = Utils.getBold(this)
        binding!!.npFt.setSelectedTypeface(Utils.getBold(this))
        binding!!.npFt.typeface = Utils.getBold(this)
        binding!!.npIN.setSelectedTypeface(Utils.getBold(this))
        binding!!.npIN.typeface = Utils.getBold(this)
        binding!!.npHeightUnit.typeface = Utils.getBold(this)
        binding!!.npHeightUnit.setSelectedTypeface(Utils.getBold(this))

        binding!!.npCM.value = 100

        binding!!.btnNext.setOnClickListener {
            if (binding!!.npHeightUnit.value == 0) {
                val inch = Utils.cmToInch(binding!!.npCM.value.toDouble())
                Utils.setPref(this, Constant.PREF_LAST_INPUT_FOOT, Utils.calcInchToFeet(inch))
                Utils.setPref(
                    this,
                    Constant.PREF_LAST_INPUT_INCH,
                    Utils.calcInFromInch(inch).toFloat()
                )
                Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)
            } else {
                Utils.setPref(
                    this,
                    Constant.PREF_LAST_INPUT_FOOT,
                    binding!!.npFt.value
                )
                Utils.setPref(
                    this,
                    Constant.PREF_LAST_INPUT_INCH,
                    binding!!.npIN.value.toFloat()
                )
                Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_IN)
            }


            val intent = Intent(getActivity(), BMIActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        binding!!.imgBack.setOnClickListener {
            finish()
        }

        binding!!.tvSkip.setOnClickListener {
            val intent = Intent(getActivity(), HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

}
