package com.loseweight.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.loseweight.BaseActivity
import com.loseweight.CommonQuestionActivity
import com.loseweight.R
import com.loseweight.adapter.ReplaceExerciseAdapter
import com.loseweight.databinding.BottomSheetExDetailBinding
import com.loseweight.databinding.BottomSheetReplaceExBinding
import com.loseweight.fragments.AnimationFragment
import com.loseweight.fragments.VideoFragment
import com.loseweight.objects.ExTableClass
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Utils


class ReplaceExerciseDialogFragment(
    val workoutPlanData: HomePlanTableClass?,
    var currExercise: HomeExTableClass,
    val mContext: BaseActivity
) : DialogFragment() {

    var dialogExDetailBinding: BottomSheetReplaceExBinding? = null
    var dialogDismissListener:DialogDismissListener?= null
    var replaceExerciseAdapter: ReplaceExerciseAdapter? = null
    var isReplaced = false

    init {
        dialogExDetailBinding = DataBindingUtil.inflate(
            mContext.layoutInflater,
            R.layout.bottom_sheet_replace_ex,
            null,
            false
        )
        replaceExerciseAdapter = ReplaceExerciseAdapter(mContext)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDialog()?.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        setStyle(STYLE_NO_TITLE, R.style.DialogSlideAnim)

        return dialogExDetailBinding!!.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog!!.setOnShowListener { setupBottomSheet(it) }
        return bottomSheetDialog!!
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as Dialog

        bottomSheetDialog!!.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        bottomSheetDialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        fillData(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {

        dialogExDetailBinding!!.currentEx.tvName.text = currExercise.exName

        dialogExDetailBinding!!.currentEx.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(currExercise.exPath!!)?.let { Utils.getAssetItems(mContext, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(mContext)
                //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(mContext).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                dialogExDetailBinding!!.currentEx.viewFlipper.addView(imgview)
            }
        }

        dialogExDetailBinding!!.currentEx.viewFlipper.isAutoStart = true
        dialogExDetailBinding!!.currentEx.viewFlipper.setFlipInterval(mContext.resources.getInteger(R.integer.viewfliper_animation))
        dialogExDetailBinding!!.currentEx.viewFlipper.startFlipping()

        val allExcList = mContext.dbHelper.getReplaceExList(currExercise.exId!!)

        if(currExercise!!.replaceExId.isNullOrEmpty().not())
        {
            val originalExID =  mContext.dbHelper.getOriginalPlanExID(currExercise!!.dayExId!!.toInt())

            for (i in allExcList.indices)
            {
                val item = allExcList.get(i)
                if(allExcList.get(i).exId.equals(originalExID))
                {
                    allExcList.removeAt(i)
                    allExcList.add(0,item)
                }
            }
        }
        dialogExDetailBinding!!.rvWorkOuts.layoutManager = LinearLayoutManager(mContext)
        dialogExDetailBinding!!.rvWorkOuts.setAdapter(replaceExerciseAdapter)

        replaceExerciseAdapter!!.setEventListener(object : ReplaceExerciseAdapter.EventListener {
            override fun onItemClick(item: ExTableClass, view: View) {

                var exList = arrayListOf<HomeExTableClass>()
                var ex = HomeExTableClass()

                ex.exId = item.exId
                ex.exName = item.exName
                ex.exUnit = item.exUnit
                ex.exPath = item.exPath
                ex.exDescription = item.exDescription
                ex.exVideo = item.exVideo
                ex.exTime = item.exTime
                ex.updatedExTime = item.exReplaceTime

                exList.add(ex)

                val exerciseDetailDialog = ExerciseDetailDialogFragment.newInstance(workoutPlanData, exList,mContext,false,true)

                exerciseDetailDialog.setOnEventListener(object : ExerciseDetailDialogFragment.DialogDismissListener{
                    override fun onDismissListener(exerciseList: MutableList<HomeExTableClass>) {
                        if(exerciseDetailDialog.isEditted) {
                            isReplaced = true
                            currExercise = exerciseList[0]
                            dismiss()
                            /*isSaveDialogShow = true
                            //editPlanAdapter!!.clear()
                            editPlanAdapter!!.addAll(exerciseList.toCollection(ArrayList<HomeExTableClass>()))*/
                        }
                    }
                })

                exerciseDetailDialog.show(0,childFragmentManager,true)

            }

        })
        replaceExerciseAdapter!!.addAll(allExcList)
    }


    fun show(currExercise: HomeExTableClass, fragmentManager: FragmentManager) {
        this.currExercise = currExercise
        fragmentManager.executePendingTransactions()
        this.show(fragmentManager, "")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissListener?.onDismissListener(currExercise)
    }

    fun setOnEventListener(listener:DialogDismissListener){
        dialogDismissListener = listener
    }

    private fun fillData(b: Boolean) {


    }

    interface DialogDismissListener{

        fun onDismissListener(currExercise: HomeExTableClass)
    }

    companion object {
        fun newInstance(
            item: HomePlanTableClass?,
            currExercise: HomeExTableClass,
            mContext: BaseActivity
        ): ReplaceExerciseDialogFragment {
            return ReplaceExerciseDialogFragment(item, currExercise, mContext)
        }
    }
}