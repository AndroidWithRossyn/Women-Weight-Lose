package com.loseweight

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.swipedragrecyclerview.OnDragListener
import com.common.swipedragrecyclerview.OnSwipeListener
import com.common.swipedragrecyclerview.RecyclerHelper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loseweight.adapter.EditPlanAdapter
import com.loseweight.databinding.ActivityEditPlanBinding
import com.loseweight.dialog.ExerciseDetailDialogFragment
import com.loseweight.dialog.ReplaceExerciseDialogFragment
import com.loseweight.interfaces.CallbackListener
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper


class EditPlanActivity : BaseActivity(), CallbackListener {

    var binding: ActivityEditPlanBinding? = null
    var editPlanAdapter: EditPlanAdapter? = null
    var workoutPlanData: HomePlanTableClass? = null
    var isSaveDialogShow = false
    var touchHelper: RecyclerHelper<*>? = null

    var deletedEx = arrayListOf<HomeExTableClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_plan)

//        AdUtils.loadBannerAd(binding!!.adView,this)
//        AdUtils.loadBannerGoogleAd(this,binding!!.llAdView,Constant.BANNER_TYPE)
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
        binding!!.topbar.isBackShow = true
        binding!!.topbar.isResetShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.edit_plan)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        editPlanAdapter = EditPlanAdapter(this)
        binding!!.rvWorkOuts.layoutManager = LinearLayoutManager(this)
        binding!!.rvWorkOuts.setAdapter(editPlanAdapter)

        editPlanAdapter!!.setEventListener(object : EditPlanAdapter.EventListener {
            override fun onItemClick(item: HomeExTableClass, view: View) {

                val exerciseDetailDialog = ExerciseDetailDialogFragment.newInstance(workoutPlanData, editPlanAdapter!!.data,this@EditPlanActivity,true)

                exerciseDetailDialog.setOnEventListener(object : ExerciseDetailDialogFragment.DialogDismissListener{
                    override fun onDismissListener(exerciseList: MutableList<HomeExTableClass>) {
                        if(exerciseDetailDialog.isEditted) {
                            isSaveDialogShow = true
                            //editPlanAdapter!!.clear()
                            editPlanAdapter!!.addAll(exerciseList.toCollection(ArrayList<HomeExTableClass>()))
                        }
                    }
                })

                exerciseDetailDialog.show(editPlanAdapter!!.data.indexOf(item),getSupportFragmentManager(),false)
            }

            override fun onReplaceClick(item: HomeExTableClass, view: View) {
                val replaceExerciseDialog = ReplaceExerciseDialogFragment.newInstance(workoutPlanData, item,this@EditPlanActivity)

                replaceExerciseDialog.setOnEventListener(object : ReplaceExerciseDialogFragment.DialogDismissListener{
                    override fun onDismissListener(currExercise: HomeExTableClass) {
                        if(replaceExerciseDialog.isReplaced) {
                            isSaveDialogShow = true
                            //editPlanAdapter!!.clear()
                            editPlanAdapter!!.replace(item,currExercise)
                        }
                    }
                })

                replaceExerciseDialog.show(item,getSupportFragmentManager())
            }

            override fun onDeleteClick(item: HomeExTableClass, view: View) {
                item.isDeleted = "1"
                deletedEx.add(item)
                editPlanAdapter!!.removeAt(editPlanAdapter!!.data.indexOf(item))
            }

        })


        fillData()
        getExerciseData()
    }

    private fun getExerciseData() {
        if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
            val dayId = intent.getStringExtra(Constant.extra_day_id)
            if (dayId != null) {
                var arrDayExTableClass = DataHelper(this).getDayExList(dayId)
                editPlanAdapter!!.addAll(arrDayExTableClass)
            }
        } else {
            editPlanAdapter!!.addAll(DataHelper(this).getHomeDetailExList(workoutPlanData!!.planId!!))
        }

        deletedEx.clear()

        touchHelper = RecyclerHelper<HomeExTableClass>(
            editPlanAdapter!!.data!!,
            editPlanAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )
        touchHelper!!.setRecyclerItemDragEnabled(true)
            .setOnDragItemListener(object : OnDragListener {
                override fun onDragItemListener(fromPosition: Int, toPosition: Int) {
                    editPlanAdapter!!.onChangePosition(fromPosition,toPosition)
                   isSaveDialogShow = true
                }
            })
        touchHelper!!.setRecyclerItemSwipeEnabled(false)
            .setOnSwipeItemListener(object : OnSwipeListener {
                override fun onSwipeItemListener() {

                }
            })
        val itemTouchHelper = ItemTouchHelper(touchHelper!!)
        itemTouchHelper.attachToRecyclerView(binding!!.rvWorkOuts)
    }

    private fun fillData() {
        if (workoutPlanData != null) {
            /*binding!!.tvWorkoutTime.text =
                workoutPlanData!!.planMinutes + " " + getString(R.string.mins)
            binding!!.tvTotalWorkout.text =
                workoutPlanData!!.planWorkouts + " " + getString(R.string.workouts)*/
        }
    }

    override fun onResume() {

        super.onResume()
    }


    inner class ClickHandler {

        fun onSaveClick() {
            saveExercise()
        }

    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                if (isSaveDialogShow)
                    showSaveConfirmationDialog()
                else
                    finish()
            }

            if (value.equals(getString(R.string.reset))) {
                if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
                    dbHelper.resetPlanDay(editPlanAdapter!!.data)
                }else{
                    dbHelper.resetPlanExc(editPlanAdapter!!.data)
                }
                getExerciseData()
                setResult(Activity.RESULT_OK)
            }

        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        if (isSaveDialogShow)
            showSaveConfirmationDialog()
        else
            super.onBackPressed()
    }

    private fun showSaveConfirmationDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.save_changes_que))
        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            saveExercise()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> finish() })
        builder.create().show()
    }

    private fun saveExercise() {
        for (i in 0..editPlanAdapter!!.data!!.size - 1) {
            val item = editPlanAdapter!!.data[i]
            item.planSort = (i + 1).toString()

            if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
                dbHelper.updateDayPlanEx(item)
            }else
                dbHelper.updatePlanEx(item)
        }

        if(deletedEx.isNullOrEmpty().not()) {
            for (i in 0..deletedEx.size - 1) {
                val item = deletedEx[i]
                item.isDeleted = "1"

                if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
                    dbHelper.updateDayPlanEx(item)
                } else
                    dbHelper.updatePlanEx(item)
            }
        }
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 8989 && resultCode == Activity.RESULT_OK) {
            showToast("Exercise Replaced successfully")
            setResult(Activity.RESULT_OK)
            getExerciseData()
        }
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }

}
