package com.loseweight

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.admision.apputils.permission.PermissionHelper
import com.bumptech.glide.Glide
import com.common.view.CEditTextView
import com.common.view.CMTextView
import com.common.view.CTextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.loseweight.databinding.*
import com.loseweight.interfaces.AdsCallback
import com.loseweight.interfaces.CallbackListener
import com.loseweight.interfaces.DateEventListener
import com.loseweight.interfaces.DialogDismissListener
import com.loseweight.objects.HomeExTableClass
import com.loseweight.utils.*
import com.loseweight.utils.CommonConstantAd.loadRewardedAdGoogle
import com.loseweight.utils.CommonConstantAd.showRewardedAdGoogle
import com.loseweight.utils.permission.ManagePermissionsImp
import com.utillity.db.DataHelper
import java.util.*
import kotlin.math.roundToInt


open class BaseActivity() : AppCompatActivity() {

    val TAG_BASE = BaseActivity::class.java.name + Constant.ARROW
    internal lateinit var commonReciever: MyEventServiceReciever
    var topBarBinding: TopbarBinding? = null
    lateinit var dbHelper: DataHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constant.FINISH_ACTIVITY)
        commonReciever = MyEventServiceReciever()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            commonReciever, intentFilter
        )

        dbHelper = DataHelper(this)
        MobileAds.initialize(this) {}
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.setLocale(newBase))
    }

    open fun getActivity(): BaseActivity {
        return this
    }

    fun loadBannerAd(llAdView:RelativeLayout,llAdViewFacebook:LinearLayout) {
        if(Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, Constant.ENABLE_DISABLE) == Constant.ENABLE && !Utils.isPurchased(this)) {
            val str = Utils.getPref(this, Constant.AD_TYPE_FB_GOOGLE, "")
            if (str == Constant.AD_GOOGLE) {
                CommonConstantAd.loadBannerGoogleAd(
                    this,
                    llAdView,
                    Utils.getPref(this, Constant.GOOGLE_BANNER, "")!!,
                    Constant.GOOGLE_BANNER_TYPE_AD
                )
            } else if (str == Constant.AD_FACEBOOK) {
                Debug.e(
                    "TAG",
                    "onCreate::::Fb Else:::   " + Utils.getPref(this, Constant.FB_BANNER, "")
                )
                CommonConstantAd.loadFbAdFacebook(
                    this,
                    llAdViewFacebook,
                    Utils.getPref(this, Constant.FB_BANNER, "")!!,
                    Constant.FB_BANNER_TYPE_AD
                )
            }
        }
    }

    fun initBack() {
        findViewById<AppCompatImageView>(R.id.imgBack_)!!.visibility = View.VISIBLE
        findViewById<AppCompatImageView>(R.id.imgBack_)!!.setOnClickListener {
            finishActivity()
        }
    }


    internal lateinit var toast: Toast

    fun showToast(text: String, duration: Int) {
        runOnUiThread {
            toast.setText(text)
            toast.duration = duration
            toast.show()
        }
    }

    fun showToast(text: String) {
        runOnUiThread {
            toast.setText(text)
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    fun showToast(strResId: Int) {
        runOnUiThread {
            toast.setText(getString(strResId))
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    fun setScreenTitle(text: String) {
        findViewById<CMTextView>(R.id.tvTitleText_)!!.text = text
    }


    fun finishActivity() {
        if (getActivity() is HomeActivity) {
        } else {
            getActivity().finish()
        }
    }


    interface TokenInterface {
        fun onSuccess(token: String?)
        fun onFailure()
    }

    internal var ad: RetrofitProgressDialog? = null

    fun showDialog(msg: String) {
        try {
            if (ad != null && ad!!.isShowing) {
                return
            }
            if (ad == null) {
                ad = RetrofitProgressDialog.getInstant(getActivity())
            }
            ad!!.show(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissDialog() {
        try {
            if (ad != null) {
                ad!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal inner class MyEventServiceReciever : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.action!!.equals(Constant.FINISH_ACTIVITY, ignoreCase = true)) {
                    finish()
                    //                    Intent i = new Intent(context,
                    //                            LoginActivity.class);
                    //                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    //                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //                    context.startActivity(i);
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startSyncing() {
        loadAnimations()
    }

    var rotation: Animation? = null

    private fun loadAnimations() {
        AnimationUtils()
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)

        try {
            if (topBarBinding != null) {
                topBarBinding!!.imgSync.visibility = View.VISIBLE
                topBarBinding!!.imgSync.startAnimation(rotation)
                topBarBinding!!.isInterNetAvailable = true
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun stopSyncing() {
        try {
            if (rotation != null) {
                rotation!!.cancel()
                if (topBarBinding != null) {
                    topBarBinding!!.imgSync.visibility = View.GONE
                    topBarBinding!!.imgSync.setImageResource(0)
                    topBarBinding!!.isInterNetAvailableShow = false
                    topBarBinding!!.isInterNetAvailable = true
                    topBarBinding!!.isInterNetAvailableShow = true
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }


    override fun onResume() {

        if (dialogPermission == null)
            checkPermissions(getActivity())
        else if (dialogPermission != null && dialogPermission!!.isShowing.not())
            checkPermissions(getActivity())


        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onPause() {
        super.onPause()
//        (application as MyApplication).stopTimer()
    }

    fun checkPermissions(activity: AppCompatActivity) {
        // Initialize a list of required permissions to request runtime
        val list = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE

        )

        PermissionHelper.checkPermissions(
            activity,
            list,
            object : ManagePermissionsImp.IPermission {
                override fun onPermissionDenied() {
                    showAlert()
                }

                override fun onPermissionGranted() {
                    if (activity is SplashScreenActivity) {
                        activity.startapp(1000)
                    }
                }
            })
    }

    var dialogPermission: AlertDialog? = null
    private fun showAlert() {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(getString(R.string.need_permission_title))
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.err_need_permission_msg))
        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
            finish()
        }
        builder.setNeutralButton(R.string.btn_cancel) { dialog, which -> finish() }
        dialogPermission = builder.create()
        dialogPermission!!.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun isInternetAvailable(context: Context, callbackListener: CallbackListener) {
        if (Utils.isInternetConnected(context)) {
            callbackListener.onSuccess()
            return
        }
        AlertDialogHelper.showNoInternetDialog(context, callbackListener)
    }

    fun showHttpError() {
        AlertDialogHelper.showHttpExceptionDialog(getActivity())
    }


    var dialogSoundOptionBinding: DialogSoundOptionBinding? = null
    lateinit var dialogSoundOption: AlertDialog

    fun showSoundOptionDialog(mContext: Context, listner: DialogDismissListener) {
        val v: View = (mContext as Activity).getLayoutInflater()
            .inflate(R.layout.dialog_sound_option, null)
        dialogSoundOptionBinding = DataBindingUtil.bind(v)
        val pd: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        pd.setTitle(getString(R.string.sound_options))
        pd.setView(v)

        dialogSoundOptionBinding!!.switchMute.isChecked =
            Utils.getPref(this@BaseActivity, Constant.PREF_IS_SOUND_MUTE, false)
        dialogSoundOptionBinding!!.switchCoachTips.isChecked =
            Utils.getPref(this@BaseActivity, Constant.PREF_IS_COACH_SOUND_ON, true)
        dialogSoundOptionBinding!!.switchVoiceGuide.isChecked =
            Utils.getPref(this@BaseActivity, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true)

        dialogSoundOptionBinding!!.switchMute.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    dialogSoundOptionBinding!!.switchCoachTips.isChecked = false
                    dialogSoundOptionBinding!!.switchVoiceGuide.isChecked = false
                    Utils.setPref(this@BaseActivity, Constant.PREF_IS_SOUND_MUTE, true)
                } else {
                    Utils.setPref(this@BaseActivity, Constant.PREF_IS_SOUND_MUTE, false)
                }
            }

        })

        dialogSoundOptionBinding!!.switchCoachTips.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogSoundOptionBinding!!.switchMute.isChecked = false
                Utils.setPref(this, Constant.PREF_IS_COACH_SOUND_ON, true)
            } else {
                Utils.setPref(this, Constant.PREF_IS_COACH_SOUND_ON, false)
            }
        }

        dialogSoundOptionBinding!!.switchVoiceGuide.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogSoundOptionBinding!!.switchMute.isChecked = false
                Utils.setPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true)
            } else {
                Utils.setPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, false)
            }
        }

        pd.setPositiveButton(R.string.btn_ok, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                listner.onDialogDismiss()
            }

        })

        dialogSoundOption = pd.create()
        dialogSoundOption.setOnDismissListener {
            listner.onDialogDismiss()
        }
        dialogSoundOption.show()
    }

    fun showHeightWeightDialog(listner: DialogDismissListener) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_height_weight, null)

        var boolKg: Boolean
        var boolInch: Boolean

        val editWeight = dialogLayout.findViewById<CEditTextView>(R.id.editWeight)
        val tvKG = dialogLayout.findViewById<CTextView>(R.id.tvKG)
        val tvLB = dialogLayout.findViewById<CTextView>(R.id.tvLB)
        val editHeightCM = dialogLayout.findViewById<CEditTextView>(R.id.editHeightCM)
        val tvCM = dialogLayout.findViewById<CTextView>(R.id.tvCM)
        val tvIN = dialogLayout.findViewById<CTextView>(R.id.tvIN)
        val editHeightFT = dialogLayout.findViewById<CEditTextView>(R.id.editHeightFT)
        val editHeightIn = dialogLayout.findViewById<CEditTextView>(R.id.editHeightIn)
        val llInch = dialogLayout.findViewById<LinearLayout>(R.id.llInch)
        val btnCancel = dialogLayout.findViewById<Button>(R.id.btnCancel)
        val btnNext = dialogLayout.findViewById<Button>(R.id.btnNext)

        editWeight.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                /* if (hasFocus) {
                     editWeight.setText(
                         editWeight.text.toString().substringBefore(" " + Constant.DEF_KG)
                     )
                 } else {
                     editWeight.setText(editWeight.text.toString() + " " + Constant.DEF_KG)
                 }*/
            }

        })

        editHeightCM.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                /*if (hasFocus) {
                    editHeightCM.setText(
                        editHeightCM.text.toString().substringBefore(" " + Constant.DEF_CM)
                    )
                } else {
                    editHeightCM.setText(editHeightCM.text.toString() + " " + Constant.DEF_CM)
                }*/
            }

        })

        editHeightFT.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                /*if (hasFocus) {
                    editHeightFT.setText(
                        editHeightFT.text.toString().substringBefore(" " + Constant.DEF_FT)
                    )
                } else {
                    editHeightFT.setText(editHeightFT.text.toString() + " " + Constant.DEF_FT)
                }*/
            }

        })

        editHeightIn.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                /*if (hasFocus) {
                    editHeightIn.setText(
                        editHeightIn.text.toString().substringBefore(" " + Constant.DEF_IN)
                    )
                } else {
                    editHeightIn.setText(editHeightIn.text.toString() + " " + Constant.DEF_IN)
                }*/
            }

        })

        boolKg = true
        try {
            if (Utils.getPref(
                    this,
                    Constant.PREF_WEIGHT_UNIT,
                    Constant.DEF_KG
                ) == Constant.DEF_KG
            ) {

                editWeight.setText(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString()
                )

                tvKG.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvLB.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvKG.isSelected = true
                tvLB.isSelected = false
            } else {
                boolKg = false

                editWeight.setText(
                    Utils.kgToLb(
                        Utils.getPref(
                            this,
                            Constant.PREF_LAST_INPUT_WEIGHT,
                            0f
                        ).toDouble()
                    ).toString()
                )

                tvKG.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                tvLB.setTextColor(ContextCompat.getColor(this, R.color.white))

                tvKG.isSelected = false
                tvLB.isSelected = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        boolInch = false
        try {
            if (Utils.getPref(
                    this,
                    Constant.PREF_HEIGHT_UNIT,
                    Constant.DEF_CM
                ) == Constant.DEF_IN
            ) {
                boolInch = true
                editHeightCM.visibility = View.GONE
                llInch.visibility = View.VISIBLE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvIN.isSelected = true
                tvCM.isSelected = false

                editHeightFT.setText(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString()
                )
                editHeightIn.setText(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString()
                )
            } else {
                boolInch = false

                editHeightCM.visibility = View.VISIBLE
                llInch.visibility = View.GONE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.white))

                tvIN.isSelected = false
                tvCM.isSelected = true

                val inch = Utils.ftInToInch(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0),
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toDouble()
                )

                editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tvKG.setOnClickListener {
            /*try {
                if (boolInch) {
                    boolInch = false

                    editHeightCM.visibility = View.VISIBLE
                    llInch.visibility = View.GONE

                    tvIN.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvCM.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvIN.isSelected = false
                    tvCM.isSelected = true

                    var inch = 0.0
                    if (editHeightFT.text.toString() != "" && editHeightFT.text.toString() != "") {
                        inch = Utils.ftInToInch(
                            editHeightFT.text.toString().toInt(),
                            editHeightIn.text.toString().toDouble()
                        )
                    } else if (editHeightFT.text.toString() != "" && editHeightIn.text.toString() == "") {
                        inch = Utils.ftInToInch(editHeightFT.text.toString().toInt(), 0.0)
                    } else if (editHeightFT.text.toString() == "" && editHeightIn.text.toString() != "") {
                        inch = Utils.ftInToInch(1, editHeightIn.text.toString().toDouble())
                    }

                    editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/

            try {
                if (!boolKg) {
                    boolKg = true

                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.white))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                    tvKG.isSelected = true
                    tvLB.isSelected = false

                    editWeight.hint = Constant.DEF_KG

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                            Utils.lbToKg(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvLB.setOnClickListener {
            try {
                if (boolKg) {
                    boolKg = false


                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvKG.isSelected = false
                    tvLB.isSelected = true

                    editWeight.hint = Constant.DEF_LB

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                            Utils.kgToLb(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

           /* try {
                editHeightCM.visibility = View.GONE
                llInch.visibility = View.VISIBLE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvIN.isSelected = true
                tvCM.isSelected = false

                try {
                    if (!boolInch) {
                        boolInch = true

                        if (editHeightCM.text.toString() != "") {
                            val inch = Utils.cmToInch(editHeightCM.text.toString().toDouble())
                            editHeightFT.setText(Utils.calcInchToFeet(inch).toString())
                            editHeightIn.setText(Utils.calcInFromInch(inch).toString())
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/
        }

        tvCM.setOnClickListener {
            try {
                if (boolInch) {
                    boolInch = false

                    editHeightCM.visibility = View.VISIBLE
                    llInch.visibility = View.GONE

                    tvIN.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvCM.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvIN.isSelected = false
                    tvCM.isSelected = true

                    var inch = 0.0
                    if (editHeightFT.text.toString() != "" && editHeightFT.text.toString() != "") {
                        inch = Utils.ftInToInch(
                            editHeightFT.text.toString().toInt(),
                            editHeightIn.text.toString().toDouble()
                        )
                    } else if (editHeightFT.text.toString() != "" && editHeightIn.text.toString() == "") {
                        inch = Utils.ftInToInch(editHeightFT.text.toString().toInt(), 0.0)
                    } else if (editHeightFT.text.toString() == "" && editHeightIn.text.toString() != "") {
                        inch = Utils.ftInToInch(1, editHeightIn.text.toString().toDouble())
                    }

                    editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /*try {
                if (!boolKg) {
                    boolKg = true

                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.white))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                    tvKG.isSelected = true
                    tvLB.isSelected = false

                    editWeight.hint = Constant.DEF_KG

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                            Utils.lbToKg(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/
        }

        tvIN.setOnClickListener {
            /*try {
                if (boolKg) {
                    boolKg = false


                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvKG.isSelected = false
                    tvLB.isSelected = true

                    editWeight.hint = Constant.DEF_LB

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                            Utils.kgToLb(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/

            try {
                editHeightCM.visibility = View.GONE
                llInch.visibility = View.VISIBLE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvIN.isSelected = true
                tvCM.isSelected = false

                try {
                    if (!boolInch) {
                        boolInch = true

                        if (editHeightCM.text.toString() != "") {
                            val inch = Utils.cmToInch(editHeightCM.text.toString().toDouble())
                            editHeightFT.setText(Utils.calcInchToFeet(inch).toString())
                            editHeightIn.setText(Utils.calcInFromInch(inch).toString())
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dialog.setContentView(dialogLayout)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dob = Utils.getPref(this, Constant.PREF_DOB, "")
        if (dob.isNullOrEmpty()) {
            btnNext.text = getString(R.string.next)
        } else {
            btnNext.text = getString(R.string.set)
        }


        btnNext.setOnClickListener {
            if(editWeight.text.toString().isNullOrEmpty()){
                showToast(getString(R.string.enter_weight))
            }else if(editHeightCM.text.toString().isNullOrEmpty() && editHeightFT.text.toString().isNullOrEmpty() && editHeightIn.text.toString().isNullOrEmpty()){
                showToast(getString(R.string.enter_height))
            }
            else if(boolKg && (editWeight.text.toString().toFloat() > Constant.MAX_KG || editWeight.text.toString().toFloat() < Constant.MIN_KG)){
                showToast("Please enter valid weight. It should be  between ${Constant.MIN_KG}KG to ${Constant.MAX_KG}KG")
            }else if(!boolKg && (editWeight.text.toString().toFloat() > Constant.MAX_LB || editWeight.text.toString().toFloat() < Constant.MIN_LB)){
                showToast("Please enter valid weight. It should be  between ${Constant.MIN_LB}LB to ${Constant.MAX_LB}LB")
            }else if(boolInch && (editHeightFT.text.toString().toFloat() > Constant.MAX_FT || editHeightFT.text.toString().toFloat() < Constant.MIN_FT)){
                showToast("Please enter valid height. It should be  between ${Constant.MIN_LB}Feet to ${Constant.MAX_LB}Feet")
            }else if(boolInch && ((editHeightIn.text.toString().toFloat() > Constant.MAX_IN ) || editHeightIn.text.toString().toFloat() < Constant.MIN_IN)){
                showToast("Please enter valid height. It should be  between ${Constant.MIN_IN}Inch to ${Constant.MAX_IN}Inch")
            }else if(boolInch && ((editHeightFT.text.toString().toFloat() == 0f) && editHeightIn.text.toString().toFloat() < 8)){
                showToast("Please enter valid height. It should be  Minimum 8 Inch")
            }else if(!boolInch && ((editHeightCM.text.toString().toFloat() > Constant.MAX_CM ) || editHeightCM.text.toString().toFloat() < Constant.MIN_CM)){
                showToast("Please enter valid height. It should be  between ${Constant.MIN_CM}CM to ${Constant.MAX_CM}CM")
            } else {
                try {
                    if (boolInch) {
                        Utils.setPref(
                            this,
                            Constant.PREF_LAST_INPUT_FOOT,
                            editHeightFT.text.toString().toInt()
                        )
                        Utils.setPref(
                            this,
                            Constant.PREF_LAST_INPUT_INCH,
                            editHeightIn.text.toString().toFloat()
                        )
                        Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_IN)

                    } else {
                        val inch = Utils.cmToInch(editHeightCM.text.toString().toDouble())
                        Utils.setPref(
                            this,
                            Constant.PREF_LAST_INPUT_FOOT,
                            Utils.calcInchToFeet(inch)
                        )
                        Utils.setPref(
                            this,
                            Constant.PREF_LAST_INPUT_INCH,
                            Utils.calcInFromInch(inch).toFloat()
                        )
                        Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)

                    }


                    val strKG: Float
                    if (boolKg) {
                        strKG = editWeight.text.toString().toFloat()
                        Utils.setPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                        Utils.setPref(this, Constant.PREF_LAST_INPUT_WEIGHT, strKG)
                    } else {
                        strKG =
                            Utils.lbToKg(editWeight.text.toString().toDouble()).roundToInt()
                                .toFloat()
                        Utils.setPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_LB)
                        Utils.setPref(this, Constant.PREF_LAST_INPUT_WEIGHT, strKG)
                    }

                    /*val currentDate = Utils.parseTime(Date().time, Constant.WEIGHT_TABLE_DATE_FORMAT)

                            if (dbHelper.weightExistOrNot(currentDate)) {
                                dbHelper.updateWeight(currentDate, strKG.toString(), "")
                            } else {
                                dbHelper.addUserWeight(strKG.toString(), currentDate, "")
                            }*/

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (dob.isNullOrEmpty()) {
                    showGenderDOBDialog(this, listner)
                } else {
                    listner.onDialogDismiss()
                }
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
            listner.onDialogDismiss()
        }
        dialog.show()
    }

    private val DEFAULT_MIN_YEAR = 1960
    private var yearPos = 0
    private var monthPos = 0
    private var dayPos = 0

    var yearList = arrayListOf<String?>()
    var monthList = arrayListOf<String?>()
    var dayList = arrayListOf<String?>()

    private var minYear = 0
    private var maxYear = 0
    private var viewTextSize = 0

    fun showGenderDOBDialog(
        mContext: Context,
        listner: DialogDismissListener
    ) {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        val v: View = (mContext as Activity).getLayoutInflater()
            .inflate(R.layout.dialog_gender_dob, null)
        val dialogbinding: DialogGenderDobBinding? = DataBindingUtil.bind(v)

        builder.setView(dialogbinding!!.root)

        if (Utils.getPref(this, Constant.PREF_GENDER, "").isNullOrEmpty().not()) {
            if (Utils.getPref(this, Constant.PREF_GENDER, "").equals(Constant.FEMALE)) {
                dialogbinding.tvFemale.isSelected = true
                dialogbinding.tvMale.isSelected = false
            } else {
                dialogbinding.tvMale.isSelected = true
                dialogbinding.tvFemale.isSelected = false
            }
        }

        dialogbinding.tvMale.setOnClickListener {
            dialogbinding.tvMale.isSelected = true
            dialogbinding.tvFemale.isSelected = false
        }

        dialogbinding.tvFemale.setOnClickListener {
            dialogbinding.tvFemale.isSelected = true
            dialogbinding.tvMale.isSelected = false
        }

        minYear = DEFAULT_MIN_YEAR
        viewTextSize = 25
        maxYear = Calendar.getInstance().get(Calendar.YEAR) - 10

        setSelectedDate()
        dialogbinding.tvMale.isSelected = true
        initPickerViews(dialogbinding.dobPicker)
        initDayPickerView(dialogbinding.dobPicker)

        builder.setPositiveButton(R.string.save) { dialog, which ->

            if (dialogbinding.tvMale.isSelected || dialogbinding.tvFemale.isSelected) {

                val day = dayList[dialogbinding.dobPicker.npDay.value]
                val month = monthList[dialogbinding.dobPicker.npMonth.value]
                val year = yearList[dialogbinding.dobPicker.npYear.value]

                val dob = Utils.parseTime("$day-$month-$year", "dd-mm-yyyy")

                if (dob < Date()) {

                    Utils.setPref(this, Constant.PREF_DOB, "$day-$month-$year")
                    if (dialogbinding.tvMale.isSelected)
                        Utils.setPref(this, Constant.PREF_GENDER, Constant.MALE)

                    if (dialogbinding.tvFemale.isSelected)
                        Utils.setPref(this, Constant.PREF_GENDER, Constant.FEMALE)

                    dialog.dismiss()
                    listner.onDialogDismiss()
                } else {
                    showToast("Date of birth must less then current date")
                }
            }
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which ->
            dialog.dismiss()
            listner.onDialogDismiss()
        })
        builder.setNeutralButton(R.string.previous, { dialog, which ->
            dialog.dismiss()
            showHeightWeightDialog(listner)
        })
        builder.create().show()
    }

    open fun setSelectedDate() {
        var today = Calendar.getInstance()
        if (Utils.getPref(this, Constant.PREF_DOB, "").isNullOrEmpty().not()) {
            val date = Utils.parseTime(Utils.getPref(this, Constant.PREF_DOB, "")!!, "dd-mm-yyyy")
            today.time = date
        }
        yearPos = today[Calendar.YEAR] - minYear
        monthPos = today[Calendar.MONTH]
        dayPos = today[Calendar.DAY_OF_MONTH] - 1
    }


    fun initPickerViews(dialogbinding: DialogDobBinding) {
        val yearCount: Int = maxYear - minYear
        for (i in 0 until yearCount) {
            yearList.add(format2LenStr(minYear + i))
        }
        for (j in 0..11) {
            monthList.add(format2LenStr(j + 1))
        }
        dialogbinding.npYear.setDisplayedValues(yearList.toArray(arrayOf()))
        dialogbinding.npYear.setMinValue(0)
        dialogbinding.npYear.setMaxValue(yearList.size - 1)
        dialogbinding.npYear.value = yearPos

        dialogbinding.npMonth.setDisplayedValues(monthList.toArray(arrayOf()))
        dialogbinding.npMonth.setMinValue(0)
        dialogbinding.npMonth.setMaxValue(monthList.size - 1)
        dialogbinding.npMonth.value = monthPos

    }

    /**
     * Init day item
     */
    fun initDayPickerView(dialogbinding: DialogDobBinding) {
        val dayMaxInMonth: Int
        val calendar = Calendar.getInstance()
        dayList = arrayListOf()
        calendar[Calendar.YEAR] = minYear + yearPos
        calendar[Calendar.MONTH] = monthPos

        //get max day in month
        dayMaxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until dayMaxInMonth) {
            dayList.add(format2LenStr(i + 1))
        }

        dialogbinding.npDay.setDisplayedValues(dayList.toArray(arrayOf()))
        dialogbinding.npDay.setMinValue(0)
        dialogbinding.npDay.setMaxValue(dayList.size - 1)
        dialogbinding.npDay.value = dayPos
    }

    open fun format2LenStr(num: Int): String? {
        return if (num < 10) "0$num" else num.toString()
    }

    fun showUnlockTrainingDialog(mContext: Context) {
//        loadInterstialAd()
        loadRewardedAdGoogle(this)
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)
                    && event!!.isTracking
                    && !event.isCanceled
                ) {
                    finishActivity()
                    return true
                }
                return false
            }

        })
        //dialog.setCancelable(false)

        val v: View = (mContext as Activity).getLayoutInflater()
            .inflate(R.layout.dialog_unloack_training, null)
        val dialogbinding: DialogUnloackTrainingBinding? = DataBindingUtil.bind(v)

        dialogbinding!!.imgBack.setOnClickListener {
            finish()
        }

        dialogbinding.llRemoveAds.setOnClickListener {
            val i = Intent(this, AccessAllFeaturesActivity::class.java)
            startActivity(i)
        }

        dialogbinding.llUnlockOnce.setOnClickListener {
            /*showInterstialAd(object : CallbackListener {
                override fun onSuccess() {
                    dialog.dismiss()
                }

                override fun onCancel() {
                    dialog.dismiss()
                    finish()
                }

                override fun onRetry() {

                }

            })*/

            if (Utils.isInternetConnected(mContext)) {
                if (Utils.getPref(this, Constant.AD_TYPE_FB_GOOGLE, "") == Constant.AD_GOOGLE &&
                    Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, "") == Constant.ENABLE
                ) {
                    showRewardedAdGoogle(mContext,object :AdsCallback{
                        override fun adLoadingFailed() {
                            dialog.dismiss()
                        }

                        override fun adClose() {
                            dialog.dismiss()
                        }

                        override fun startNextScreen() {
                            dialog.dismiss()
                        }
                    })
                } else if (Utils.getPref(
                        this,
                        Constant.AD_TYPE_FB_GOOGLE,
                        ""
                    ) == Constant.AD_FACEBOOK &&
                    Utils.getPref(this, Constant.STATUS_ENABLE_DISABLE, "") == Constant.ENABLE
                ) {
                    CommonConstantAd.showInterstitialAdsFacebook(object :AdsCallback{
                        override fun adLoadingFailed() {
                            dialog.dismiss()
                        }

                        override fun adClose() {
                            dialog.dismiss()
                        }

                        override fun startNextScreen() {
                            dialog.dismiss()
                        }

                    })
                }


            }else{
                showToast(getString(R.string.no_internet_available))
            }
        }

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels


        dialog.setContentView(v)
        if (Utils.isPurchased(this).not())
            dialog.show()
        dialog.window!!.setLayout(width.toInt(), height.toInt())
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadInterstialAd()

    }


    fun loadInterstialAd() {
        if(Utils.getPref(this,Constant.STATUS_ENABLE_DISABLE,"") == Constant.ENABLE) {
            if (Utils.getPref(this, Constant.AD_TYPE_FB_GOOGLE, "") == Constant.AD_GOOGLE) {
                CommonConstantAd.googlebeforloadAd(
                    this,
                    Utils.getPref(this, Constant.GOOGLE_INTERSTITIAL, "")!!
                )
            } else if (Utils.getPref(
                    this,
                    Constant.AD_TYPE_FB_GOOGLE,
                    ""
                ) == Constant.AD_FACEBOOK
            ) {
                CommonConstantAd.facebookbeforeloadFullAd(
                    this,
                    Utils.getPref(this, Constant.FB_INTERSTITIAL, "")!!
                )
            }
        }
    }

    fun showInterstialAd(listner: CallbackListener? =null,callBack:AdsCallback? = null) {

        if (Debug.DEBUG_IS_HIDE_AD) {
            listner?.onSuccess()
            return
        }

        val adsCallback = callBack
            ?: object :AdsCallback{
                override fun adLoadingFailed() {
                    listner?.onSuccess()
                }

                override fun adClose() {
                    listner?.onSuccess()

                }

                override fun startNextScreen() {
                    listner?.onSuccess()
                }

            }

        if (Utils.getPref(this,Constant.AD_TYPE_FB_GOOGLE,"") == Constant.AD_GOOGLE
                && Utils.getPref(this,Constant.STATUS_ENABLE_DISABLE,"") == Constant.ENABLE) {
            CommonConstantAd.showInterstitialAdsGoogle(this,adsCallback)
        } else if (Utils.getPref(this,Constant.AD_TYPE_FB_GOOGLE,"") == Constant.AD_FACEBOOK
                && Utils.getPref(this,Constant.STATUS_ENABLE_DISABLE,"") == Constant.ENABLE) {
            CommonConstantAd.showInterstitialAdsFacebook(adsCallback)
        }else{
            listner?.onSuccess()
        }


    }

    fun showTimePickerDialog(context: Context, date: Date, eventListener: DateEventListener?) {
        val c = Calendar.getInstance()
        c.time = date
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                Debug.e(
                    "TAG",
                    "onTimeSet() called with: view = [$view], hourOfDay = [$hourOfDay], minute = [$minute]"
                )

                //Date date = new Date(selectedyear, selectedmonth, selectedday, hourOfDay, minute, 0);
                val date = Utils.parseTime(
                    c.get(Calendar.DAY_OF_MONTH)
                        .toString() + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + " " + hourOfDay + ":" + minute + ":00",
                    "dd/MM/yyyy HH:mm:ss"
                )
                eventListener?.onDateSelected(date, hourOfDay, minute)
            }, hour, minute, false
        )
        timePicker.show()
    }


}
