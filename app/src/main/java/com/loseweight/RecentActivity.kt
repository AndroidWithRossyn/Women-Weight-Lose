package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loseweight.adapter.RecentAdapter
import com.loseweight.databinding.ActivityRecentBinding
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.objects.HistoryDetailsClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils


class RecentActivity : BaseActivity() {

    var binding: ActivityRecentBinding? = null
    var recentAdapter: RecentAdapter? = null
    var listRecentPlan = arrayListOf<HistoryDetailsClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recent)

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
        binding!!.topbar.tvTitleText.text = getString(R.string.recent)
        binding!!.topbar.topBarClickListener = TopClickListener()

        recentAdapter = RecentAdapter(this)
        binding!!.rvRecent.layoutManager = LinearLayoutManager(this)
        binding!!.rvRecent.setAdapter(recentAdapter)

        recentAdapter!!.setEventListener(object : RecentAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = recentAdapter!!.getItem(position)
               if (item!!.planDetail!!.planDays.equals("YES")) {
                    val i = Intent(this@RecentActivity, DaysPlanDetailActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(item.planDetail))
                    startActivity(i)
                } else {
                    val i = Intent(this@RecentActivity, ExercisesListActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(item.planDetail))
                    startActivity(i)
                }
            }

        })

        fillData()

    }


    private fun fillData() {
        listRecentPlan = dbHelper.getRecentHistoryList()
        recentAdapter!!.addAll(listRecentPlan)
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {


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
