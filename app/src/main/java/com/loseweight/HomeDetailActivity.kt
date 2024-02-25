package com.loseweight

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.HomeSubPlanAdapter
import com.loseweight.databinding.ActivityHomeDetailBinding
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils


class HomeDetailActivity : BaseActivity() {

    var binding: ActivityHomeDetailBinding? = null
    var homeSubPlanAdapter: HomeSubPlanAdapter? = null
    var workoutPlanData:HomePlanTableClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_detail)

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

        homeSubPlanAdapter = HomeSubPlanAdapter(this)
        binding!!.rvWorkOuts.layoutManager = LinearLayoutManager(this)
        binding!!.rvWorkOuts.setAdapter(homeSubPlanAdapter)

        homeSubPlanAdapter!!.setEventListener(object : HomeSubPlanAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = homeSubPlanAdapter!!.getItem(position)
                val i = Intent(this@HomeDetailActivity, ExercisesListActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(item))
                startActivity(i)
            }

        })

        var isShow = true
        var scrollRange = -1
        binding!!.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding!!.llTopTitle.visibility = View.VISIBLE
                binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                isShow = true
            } else if (isShow) {
                binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding!!.llTopTitle.visibility = View.GONE //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })

        fillData()

    }


    private fun fillData() {
        if (workoutPlanData != null) {

            binding!!.tvTitle.text = workoutPlanData!!.planName
            binding!!.tvDes.text = workoutPlanData!!.shortDes
            binding!!.tvTitleText.text = workoutPlanData!!.planName

            binding!!.titleImage.setImageResource(
                Utils.getDrawableId(
                    workoutPlanData!!.planImage,
                    this
                )
            )

            homeSubPlanAdapter!!.addAll(dbHelper.getHomeSubPlanList(workoutPlanData!!.planId!!))
        }
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onBackClick() {
            finish()
        }

    }


}
