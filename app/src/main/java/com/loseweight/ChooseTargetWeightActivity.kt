package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.view.numberpicker.NumberPicker
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.databinding.ActivityChooseTargetWeightBinding
import com.loseweight.databinding.ActivityChooseWeightBinding
import com.loseweight.databinding.ActivityChooseYourPlanBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import kotlin.math.roundToInt


class ChooseTargetWeightActivity : BaseActivity() {
    val TAG = ChooseTargetWeightActivity::class.java.name + Constant.ARROW

    var binding: ActivityChooseTargetWeightBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_target_weight)
        init()
    }


    private fun init() {

        val data = arrayOf("kg", "lbs")

        binding!!.npWeightUnit.wrapSelectorWheel = false
        binding!!.npWeightUnit.minValue = 0
        binding!!.npWeightUnit.maxValue = data.size - 1
        binding!!.npWeightUnit.displayedValues = data
        binding!!.npWeightUnit.value = 0

        // Set scroller enabled
        binding!!.npWeightUnit.isScrollerEnabled = true

        // Set wrap selector wheel
        binding!!.npWeightUnit.wrapSelectorWheel = false

        binding!!.npWeightUnit.setOnValueChangedListener(object :NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {

                if(picker!!.value == 1){

                    binding!!.npWeight.wrapSelectorWheel = false
                    binding!!.npWeight.minValue = 44
                    binding!!.npWeight.maxValue = 2200
                    binding!!.npWeight.value = Utils.kgToLb(binding!!.npWeight.value.toDouble()).roundToInt()

                } else if(picker.value == 0){

                    binding!!.npWeight.wrapSelectorWheel = false
                    binding!!.npWeight.minValue = 20
                    binding!!.npWeight.maxValue = 997
                    binding!!.npWeight.value = Utils.lbToKg(binding!!.npWeight.value.toDouble()).roundToInt()

                }
            }
        })

        binding!!.npWeight.minValue = 20
        binding!!.npWeight.maxValue = 997
        binding!!.npWeight.value = 60
        binding!!.npWeight.setSelectedTypeface(Utils.getBold(this))
        binding!!.npWeight.typeface = Utils.getBold(this)
        binding!!.npWeightUnit.typeface = Utils.getBold(this)
        binding!!.npWeightUnit.setSelectedTypeface(Utils.getBold(this))

        binding!!.btnNext.setOnClickListener {

            var weight = 0f
            if (binding!!.npWeightUnit.value == 0) {
                weight = binding!!.npWeight.value.toFloat()
            } else {
                weight = Utils.lbToKg(binding!!.npWeight.value.toDouble()).toFloat()
            }

            Utils.setPref(this, Constant.PREF_TARGET_WEIGHT,weight)
            val i = Intent(this,ChooseHeightActivity::class.java)
            startActivity(i)

        }

        binding!!.imgBack.setOnClickListener {
            finish()
        }

        binding!!.tvSkip.setOnClickListener {
            val intent = Intent(getActivity(), HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

}
