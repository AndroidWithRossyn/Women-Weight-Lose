package com.loseweight

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.databinding.ActivityAboutBinding
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils


class AboutActivity : BaseActivity() {

    var binding: ActivityAboutBinding? = null
    var workoutPlanData: HomePlanTableClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)

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
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()

        fillData()

    }


    private fun fillData() {
        if (workoutPlanData != null) {

            binding!!.tvIntroductionDes.text = workoutPlanData!!.introduction
            binding!!.tvTestDes.text = workoutPlanData!!.planTestDes
            binding!!.tvTitle.text = workoutPlanData!!.planName!!.substringBefore("correction") + " Test"
            binding!!.tvTest.text = workoutPlanData!!.planName!!.substringBefore("correction")

            binding!!.imgTest.setImageResource(Utils.getDrawableId(workoutPlanData!!.planImage!!.replace("cover_","img_"), this))

            val str ="This is simple self-test. For more information, please check on Wikipedia, or consult your doctor."
            val spannable: Spannable = SpannableString(str)

            val indexTermsStart: Int = str.indexOf("Wikipedia")
            val indexTermsEnd = indexTermsStart + 9
            spannable.setSpan(UnderlineSpan(), indexTermsStart, indexTermsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {

                    var url = ""
                    if(workoutPlanData!!.planName!!.equals("Knock knee correction"))
                    {
                        url = "https://en.m.wikipedia.org/wiki/Genu_valgum"
                    }else{
                        url ="https://en.m.wikipedia.org/wiki/Genu_varum"
                    }
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)

                }
            }, indexTermsStart, indexTermsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(Color.BLUE), indexTermsStart, indexTermsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding!!.tvWikiPedia.text = spannable
            binding!!.tvWikiPedia.setClickable(true)
            binding!!.tvWikiPedia.setMovementMethod(LinkMovementMethod.getInstance())
        }
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onCancelClick() {
            finish()
        }

    }


}
