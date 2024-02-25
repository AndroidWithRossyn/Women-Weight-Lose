package com.loseweight

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.common.view.numberpicker.NumberPicker
import com.loseweight.databinding.ActivityMyProfileBinding
import com.loseweight.databinding.DialogHeightBinding
import com.loseweight.databinding.DialogWeightBinding
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import kotlin.math.roundToInt


class MyProfileActivity : BaseActivity() {

    var binding: ActivityMyProfileBinding? = null
    var boolKg = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isBackShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.my_profile)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        boolKg = Utils.getPref(
            this,
            Constant.PREF_WEIGHT_UNIT,
            Constant.DEF_KG
        ) == Constant.DEF_KG
        fillData()
    }

    private fun fillData() {

        try {
            if (boolKg) {

                binding!!.editWeight.setText(
                    Utils.truncateUptoTwoDecimal(Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString())+ " KG"
                )

                binding!!.editTargetWeight.setText(
                    Utils.truncateUptoTwoDecimal(Utils.getPref(this, Constant.PREF_TARGET_WEIGHT, 0f).toString())+ " KG"
                )

                val inch = Utils.ftInToInch(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0),
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toDouble()
                )

                binding!!.editHeight.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString() +" CM")

                binding!!.tvKgCm.background = ContextCompat.getDrawable(this@MyProfileActivity,R.drawable.bg_primary_left_7)
                binding!!.tvKgCm.setTextColor(ContextCompat.getColor(this@MyProfileActivity,R.color.white))
                binding!!.tvLbsFt.setTextColor(ContextCompat.getColor(this@MyProfileActivity,R.color.primary))
                binding!!.tvLbsFt.background = ContextCompat.getDrawable(this@MyProfileActivity,R.drawable.bg_primary_border_right_7)
            } else {

                binding!!.editWeight.setText(
                    Utils.truncateUptoTwoDecimal( Utils.kgToLb(
                        Utils.getPref(
                            this,
                            Constant.PREF_LAST_INPUT_WEIGHT,
                            0f
                        ).toDouble()
                    ).toString())+" Lbs"
                )

                binding!!.editTargetWeight.setText(
                    Utils.truncateUptoTwoDecimal(Utils.kgToLb(
                        Utils.getPref(
                            this,
                            Constant.PREF_TARGET_WEIGHT,
                            0f
                        ).toDouble()
                    ).toString())+" Lbs"
                )

                binding!!.editHeight.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString()+"'' "+Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString())

                binding!!.tvKgCm.background = ContextCompat.getDrawable(this@MyProfileActivity,R.drawable.bg_primary_border_left_7)
                binding!!.tvKgCm.setTextColor(ContextCompat.getColor(this@MyProfileActivity,R.color.primary))
                binding!!.tvLbsFt.setTextColor(ContextCompat.getColor(this@MyProfileActivity,R.color.white))
                binding!!.tvLbsFt.background = ContextCompat.getDrawable(this@MyProfileActivity,R.drawable.bg_primary_right_7)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onKgCmClick()
        {
            boolKg =true
           fillData()
        }

        fun onLbsFtClick()
        {
            boolKg =false
            fillData()
        }

        fun onHeightClick(){
            showHeightDialog()
        }

        fun onWeightClick(){
            showWeightDialog(false)
        }

        fun onTargetWeightClick(){
            showWeightDialog(true)
        }

    }

    var dialogWeightBinding: DialogWeightBinding? = null
    lateinit var dialogWeight: AlertDialog
    private fun showWeightDialog(isFromTarget:Boolean) {
        val v: View = getLayoutInflater()
            .inflate(R.layout.dialog_weight, null)

        dialogWeightBinding = DataBindingUtil.bind(v)
        val pd: AlertDialog.Builder  = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        pd.setView(v)

        val data = arrayOf("kg", "lbs")

        dialogWeightBinding!!.npWeightUnit.wrapSelectorWheel = false
        dialogWeightBinding!!.npWeightUnit.minValue = 0
        dialogWeightBinding!!.npWeightUnit.maxValue = data.size - 1
        dialogWeightBinding!!.npWeightUnit.displayedValues = data
        dialogWeightBinding!!.npWeightUnit.value = 0

        // Set scroller enabled
        dialogWeightBinding!!.npWeightUnit.isScrollerEnabled = true

        // Set wrap selector wheel
        dialogWeightBinding!!.npWeightUnit.wrapSelectorWheel = false

        dialogWeightBinding!!.npWeightUnit.setOnValueChangedListener(object :NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {

                if(picker!!.value == 1){

                   dialogWeightBinding!!.npWeight.wrapSelectorWheel = false
                    dialogWeightBinding!!.npWeight.minValue = Constant.MIN_LB.toInt()
                    dialogWeightBinding!!.npWeight.maxValue = Constant.MAX_LB.toInt()
                   dialogWeightBinding!!.npWeight.value = Utils.kgToLb(dialogWeightBinding!!.npWeight.value.toDouble()).roundToInt()

                } else if(picker.value == 0){

                  dialogWeightBinding!!.npWeight.wrapSelectorWheel = false
                    dialogWeightBinding!!.npWeight.minValue = Constant.MIN_KG
                    dialogWeightBinding!!.npWeight.maxValue = Constant.MAX_KG
                  dialogWeightBinding!!.npWeight.value = Utils.lbToKg(dialogWeightBinding!!.npWeight.value.toDouble()).roundToInt()

                }

            }
        })

        dialogWeightBinding!!.npWeight.minValue = Constant.MIN_KG
        dialogWeightBinding!!.npWeight.maxValue = Constant.MAX_KG
        if(isFromTarget)
        dialogWeightBinding!!.npWeight.value = Utils.getPref(this, Constant.PREF_TARGET_WEIGHT, 0f).roundToInt()
        else
        dialogWeightBinding!!.npWeight.value = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).roundToInt()
        //dialogWeightBinding!!.npWeight.setSelectedTypeface(Utils.getBold(this))
        //dialogWeightBinding!!.npWeight.typeface = Utils.getBold(this)
        //dialogWeightBinding!!.npWeightUnit.typeface = Utils.getBold(this)
        //dialogWeightBinding!!.npWeightUnit.setSelectedTypeface(Utils.getBold(this))

        dialogWeightBinding!!.llCancel.setOnClickListener {
            dialogWeight.dismiss()
        }

        dialogWeightBinding!!.llSave.setOnClickListener {

            var weightUnit = ""
            var weight = 0f
            if (dialogWeightBinding!!.npWeightUnit.value == 0) {
                weightUnit = Constant.DEF_KG
                weight = dialogWeightBinding!!.npWeight.value.toFloat()
            } else {
                weightUnit = Constant.DEF_LB
                weight = Utils.lbToKg(dialogWeightBinding!!.npWeight.value.toDouble()).toFloat()
            }

            if(isFromTarget){
                Utils.setPref(this, Constant.PREF_TARGET_WEIGHT, weight)
            }else {
                Utils.setPref(this, Constant.PREF_LAST_INPUT_WEIGHT, weight)
                Utils.setPref(this, Constant.PREF_WEIGHT_UNIT, weightUnit)
            }

            fillData()
            dialogWeight.dismiss()
        }


        dialogWeight = pd.create()
        dialogWeight.show()
    }

    var dialogHeightBinding:DialogHeightBinding? = null
    lateinit var dialogHeight: AlertDialog
    private fun showHeightDialog() {
        val v: View = getLayoutInflater()
            .inflate(R.layout.dialog_height, null)

        dialogHeightBinding = DataBindingUtil.bind(v)
        val pd: AlertDialog.Builder  = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        pd.setView(v)

        val data = arrayOf("cm", " ft+in")

        dialogHeightBinding!!.npHeightUnit.wrapSelectorWheel = false
        dialogHeightBinding!!.npHeightUnit.minValue = 0
        dialogHeightBinding!!.npHeightUnit.maxValue = data.size - 1
        dialogHeightBinding!!.npHeightUnit.displayedValues = data
        dialogHeightBinding!!.npHeightUnit.value = 0


        dialogHeightBinding!!.npFt.minValue = Constant.MIN_FT
        dialogHeightBinding!!.npFt.maxValue = Constant.MAX_FT

        dialogHeightBinding!!.npIN.minValue = Constant.MIN_IN.toInt()
        dialogHeightBinding!!.npIN.maxValue = Constant.MAX_IN.toInt()

        // Set scroller enabled
        dialogHeightBinding!!.npHeightUnit.isScrollerEnabled = true

        // Set wrap selector wheel
        dialogHeightBinding!!.npHeightUnit.wrapSelectorWheel = false

        dialogHeightBinding!!.npHeightUnit.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {

                if(picker!!.value == 1){

                    dialogHeightBinding!!.npCM.visibility = View.GONE
                    dialogHeightBinding!!.npFt.visibility = View.VISIBLE
                    dialogHeightBinding!!.npIN.visibility = View.VISIBLE
                    dialogHeightBinding!!.tvFt.visibility = View.VISIBLE
                    dialogHeightBinding!!.tvIn.visibility = View.VISIBLE

                    val inch = Utils.cmToInch(dialogHeightBinding!!.npCM.value.toDouble())
                    dialogHeightBinding!!.npFt.value = Utils.calcInchToFeet(inch)
                    dialogHeightBinding!!.npIN.value = Utils.calcInFromInch(inch).roundToInt()

                } else if(picker.value == 0){

                    dialogHeightBinding!!.npCM.visibility = View.VISIBLE
                    dialogHeightBinding!!.npFt.visibility = View.GONE
                    dialogHeightBinding!!.npIN.visibility = View.GONE
                    dialogHeightBinding!!.tvFt.visibility = View.GONE
                    dialogHeightBinding!!.tvIn.visibility = View.GONE

                    val inch = Utils.ftInToInch(
                        dialogHeightBinding!!.npFt.value,
                        dialogHeightBinding!!.npIN.value.toDouble()
                    )

                    dialogHeightBinding!!.npFt.minValue = Constant.MIN_FT
                    dialogHeightBinding!!.npFt.maxValue = Constant.MAX_FT

                    dialogHeightBinding!!.npIN.minValue = Constant.MIN_IN.toInt()
                    dialogHeightBinding!!.npIN.maxValue = Constant.MAX_IN.toInt()

                    dialogHeightBinding!!.npCM.value = Utils.inchToCm(inch).roundToInt()
                }

            }
        })

        /*dialogHeightBinding!!.npCM.setSelectedTypeface(Utils.getBold(this))
        dialogHeightBinding!!.npCM.typeface = Utils.getBold(this)
        dialogHeightBinding!!.npFt.setSelectedTypeface(Utils.getBold(this))
        dialogHeightBinding!!.npFt.typeface = Utils.getBold(this)
        dialogHeightBinding!!.npIN.setSelectedTypeface(Utils.getBold(this))
        dialogHeightBinding!!.npIN.typeface = Utils.getBold(this)
        dialogHeightBinding!!.npHeightUnit.typeface = Utils.getBold(this)
        dialogHeightBinding!!.npHeightUnit.setSelectedTypeface(Utils.getBold(this))*/

        dialogHeightBinding!!.npCM.value = 100

        dialogHeightBinding!!.llCancel.setOnClickListener {



            dialogHeight.dismiss()
        }

        dialogHeightBinding!!.llSave.setOnClickListener {

            if (dialogHeightBinding!!.npHeightUnit.value == 0) {
                val inch = Utils.cmToInch(dialogHeightBinding!!.npCM.value.toDouble())
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
                    dialogHeightBinding!!.npFt.value
                )
                Utils.setPref(
                    this,
                    Constant.PREF_LAST_INPUT_INCH,
                    dialogHeightBinding!!.npIN.value.toFloat()
                )
                Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_IN)
            }

            fillData()
            dialogHeight.dismiss()
        }

        dialogHeight = pd.create()
        dialogHeight.show()

    }



    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
               finish()
            }

        }
    }

}
