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
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.common.view.CMTextView
import com.google.android.material.tabs.TabLayout
import com.loseweight.CommonQuestionActivity
import com.loseweight.R
import com.loseweight.databinding.BottomSheetExDetailBinding
import com.loseweight.fragments.AnimationFragment
import com.loseweight.fragments.VideoFragment
import com.loseweight.objects.HomeExTableClass
import com.loseweight.objects.HomePlanTableClass
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils

class ExerciseDetailDialogFragment(
    val item: HomePlanTableClass?,
    val exerciseList: MutableList<HomeExTableClass>,
    val mContext: Activity,
    val fromEdit:Boolean = false,
    val fromReplace:Boolean = false
) : DialogFragment() {

    var dialogExDetailBinding: BottomSheetExDetailBinding? = null
    var pagerAdapter: ScreenSlidePagerAdapter? = null
    var dialogDismissListener:DialogDismissListener?= null
    lateinit var tabTitles: ArrayList<String>
    var currPos = 0
    var isSingle = false
    var isEditted = false

    var second:Int? = null

    init {
        dialogExDetailBinding = DataBindingUtil.inflate(
            mContext.layoutInflater,
            R.layout.bottom_sheet_ex_detail,
            null,
            false
        )
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

        tabTitles = ArrayList()
        tabTitles.add("Animation")
        tabTitles.add("Video")

        if(fromEdit || fromReplace)
        {
            dialogExDetailBinding!!.imgMinus.visibility = View.VISIBLE
            dialogExDetailBinding!!.imgAdd.visibility = View.VISIBLE
        }

        if(fromReplace) {
            dialogExDetailBinding!!.llReplace.visibility = View.VISIBLE
        }

        pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        dialogExDetailBinding!!.pagerFragment.offscreenPageLimit = pagerAdapter!!.count
        dialogExDetailBinding!!.pagerFragment.adapter = pagerAdapter
        dialogExDetailBinding!!.pagerFragment.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            //
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            //
            override fun onPageSelected(position: Int) {
                changeTabUi(position)
            }
//
        })

        dialogExDetailBinding!!.tabTransit.setupWithViewPager(dialogExDetailBinding!!.pagerFragment)

        dialogExDetailBinding!!.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        dialogExDetailBinding!!.btnContinue.setOnClickListener {
            dialog?.dismiss()
        }

        dialogExDetailBinding!!.imgNext.setOnClickListener {
            if (currPos < (exerciseList.size - 1)) {
                currPos += 1
                fillData(true)
            }
        }

        dialogExDetailBinding!!.imgPrev.setOnClickListener {

            if (currPos > 0) {
                currPos -= 1
                fillData(true)
            }

        }

        dialogExDetailBinding!!.peekView.setOnClickListener {
            dialog?.dismiss()
        }

        dialogExDetailBinding!!.tvCommonQuestion.setOnClickListener {
           val i = Intent(mContext,CommonQuestionActivity::class.java)
            mContext.startActivity(i)
        }

        dialogExDetailBinding!!.imgMinus.setOnClickListener {

            if (exerciseList[currPos].exUnit.equals(Constant.workout_type_step)) {
                if(second != null && second!! > 1)
                    second = second?.minus(1)
                dialogExDetailBinding!!.tvTime.text = "${second}"
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.repeat)
            } else {
                if(second != null && second!! > 10)
                second = second?.minus(5)
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.duration)
                dialogExDetailBinding!!.tvTime.text = Utils.secToString(
                    second!!,
                    Constant.WORKOUT_TIME_FORMAT
                )
            }

            if(fromEdit) {
                if (second != exerciseList[currPos].exTime?.toInt()) {
                    dialogExDetailBinding!!.llPrevNext.visibility = View.GONE
                    dialogExDetailBinding!!.llSave.visibility = View.VISIBLE
                } else {
                    dialogExDetailBinding!!.llPrevNext.visibility = View.VISIBLE
                    dialogExDetailBinding!!.llSave.visibility = View.GONE
                }
            }
        }

        dialogExDetailBinding!!.imgAdd.setOnClickListener {

            if (exerciseList[currPos].exUnit.equals(Constant.workout_type_step)) {
                    second = second?.plus(1)
                dialogExDetailBinding!!.tvTime.text = "${second}"
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.repeat)
            } else {
                    second = second?.plus(5)
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.duration)
                dialogExDetailBinding!!.tvTime.text = Utils.secToString(
                    second!!,
                    Constant.WORKOUT_TIME_FORMAT
                )
            }

            if(fromEdit) {
                if (second != exerciseList[currPos].exTime?.toInt()) {
                    dialogExDetailBinding!!.llPrevNext.visibility = View.GONE
                    dialogExDetailBinding!!.llSave.visibility = View.VISIBLE
                } else {
                    dialogExDetailBinding!!.llPrevNext.visibility = View.VISIBLE
                    dialogExDetailBinding!!.llSave.visibility = View.GONE
                }
            }
        }

        dialogExDetailBinding!!.btnReset.setOnClickListener{

            second = exerciseList[currPos].exTime?.toInt()
            if (exerciseList[currPos].exUnit.equals(Constant.workout_type_step)) {
                dialogExDetailBinding!!.tvTime.text = "${second}"
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.repeat)
            } else {
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.duration)
                dialogExDetailBinding!!.tvTime.text = Utils.secToString(
                    second!!,
                    Constant.WORKOUT_TIME_FORMAT
                )
            }

            dialogExDetailBinding!!.llPrevNext.visibility = View.VISIBLE
            dialogExDetailBinding!!.llSave.visibility = View.GONE
        }

        dialogExDetailBinding!!.btnSave.setOnClickListener{

            exerciseList[currPos].exTime = second.toString()
            isEditted = true
            if (exerciseList[currPos].exUnit.equals(Constant.workout_type_step)) {
                dialogExDetailBinding!!.tvTime.text = "${second}"
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.repeat)
            } else {
                dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.duration)
                dialogExDetailBinding!!.tvTime.text = Utils.secToString(
                    second!!,
                    Constant.WORKOUT_TIME_FORMAT
                )
            }

            dialogExDetailBinding!!.llPrevNext.visibility = View.VISIBLE
            dialogExDetailBinding!!.llSave.visibility = View.GONE

            dialog?.dismiss()
        }

        dialogExDetailBinding!!.btnReplace.setOnClickListener{

            exerciseList[currPos].exTime = second.toString()
            isEditted = true

            dialog?.dismiss()
        }

        setUpTabs()
    }

    private fun setUpTabs() {
        for (i in 0..tabTitles.size - 1) {
            dialogExDetailBinding!!.tabTransit.getTabAt(i)?.setCustomView(prepareTabView(i))
        }
        val currentTab: TabLayout.Tab? = dialogExDetailBinding!!.tabTransit.getTabAt(0)
        if (currentTab != null) {
            val customView = currentTab.customView
            if (customView != null) {
                customView.isSelected = true
            }
            currentTab.select()
        }
        changeTabUi(0)
    }

    private fun changeTabUi(position: Int) {
        for (i in 0 until dialogExDetailBinding!!.tabTransit.getTabCount()) {
            val view: View? = dialogExDetailBinding!!.tabTransit.getTabAt(i)?.getCustomView()
            if (view != null) {
                val tvTabTitle = view.findViewById<TextView>(R.id.tvTabTitle)
                if (position == i) {
                    tvTabTitle.setTypeface(context?.let { Utils.getBold(it) })
                } else {
                    tvTabTitle.setTypeface(context?.let { Utils.getMedium(it) })
                }
            }
        }
    }


    fun prepareTabView(pos: Int): View? {
        val inflater = LayoutInflater.from(context)
        val view = inflater!!.inflate(R.layout.custom_tab, null, false)
        view.findViewById<CMTextView>(R.id.tvTabTitle).text = tabTitles[pos]
        view.findViewById<CMTextView>(R.id.tvTabTitle).setOnClickListener {
            dialogExDetailBinding!!.pagerFragment.setCurrentItem(pos, true)
        }
        if (pos == 0) {
            view.findViewById<CMTextView>(R.id.tvTabTitle).isSelected = true
        }

        return view
    }


    inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(
        fm,
        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getItem(position: Int): Fragment {
            var tp: Fragment? = null

            tp = if (position == 0)
                AnimationFragment.newInstance(exerciseList[currPos].exPath!!)
            else
                VideoFragment.newInstance(exerciseList[currPos].exVideo ?: "")

            return tp
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return ""
        }
    }

    fun show(pos: Int, fragmentManager: FragmentManager,isSingle:Boolean) {
        currPos = pos
        this.isSingle = isSingle
        fragmentManager.executePendingTransactions()
        this.show(fragmentManager, "")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissListener?.onDismissListener(exerciseList)
    }

    fun setOnEventListener(listener:DialogDismissListener){
        dialogDismissListener = listener
    }

    private fun fillData(b: Boolean) {
        val homeExTableClass: HomeExTableClass = exerciseList[currPos]
        dialogExDetailBinding!!.tvExerciesName.text = homeExTableClass.exName
        dialogExDetailBinding!!.tvDes.text = homeExTableClass.exDescription
        dialogExDetailBinding!!.tvCurrPosition.text = "${(currPos + 1)}/${exerciseList.size}"

        second = homeExTableClass.exTime?.toInt()


        if(isSingle)
        {
            if(!fromReplace) {
                dialogExDetailBinding!!.llPrevNext.visibility = View.GONE
                dialogExDetailBinding!!.btnContinue.visibility = View.VISIBLE
            }else{
                dialogExDetailBinding!!.llPrevNext.visibility = View.GONE
                dialogExDetailBinding!!.btnContinue.visibility = View.GONE
                dialogExDetailBinding!!.llReplace.visibility = View.VISIBLE
            }
        }else{
            dialogExDetailBinding!!.llPrevNext.visibility = View.VISIBLE
            dialogExDetailBinding!!.btnContinue.visibility = View.GONE
        }

        if(currPos == 0)
        {
            dialogExDetailBinding!!.imgPrev.setImageResource(R.drawable.ic_previous)
        }else{
            dialogExDetailBinding!!.imgPrev.setImageResource(R.drawable.ic_previous_highlight)
        }

        if(currPos == exerciseList.size-1)
        {
            dialogExDetailBinding!!.imgNext.setImageResource(R.drawable.ic_next)
        }else{
            dialogExDetailBinding!!.imgNext.setImageResource(R.drawable.ic_next_highlight)
        }

        if (homeExTableClass.exUnit.equals(Constant.workout_type_step)) {
            dialogExDetailBinding!!.tvTime.text = "${homeExTableClass.exTime}"
            dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.repeat)
        } else {
            dialogExDetailBinding!!.tvExerciesUnit.text = mContext.getString(R.string.duration)
            dialogExDetailBinding!!.tvTime.text = Utils.secToString(
                homeExTableClass.exTime!!.toInt(),
                Constant.WORKOUT_TIME_FORMAT
            )
        }

        if (b) {

            try {
                val page: Fragment? =
                    childFragmentManager.findFragmentByTag("android:switcher:" + dialogExDetailBinding!!.pagerFragment.id + ":" + 0)
                val page2: Fragment? =
                    childFragmentManager.findFragmentByTag("android:switcher:" + dialogExDetailBinding!!.pagerFragment.id + ":" + 1)

                if (page != null && page is AnimationFragment) {
                    (page as AnimationFragment).init(homeExTableClass.exPath!!)
                }

                if (page2 != null && page2 is VideoFragment) {
                    if (!homeExTableClass.exVideo.isNullOrEmpty())
                        (page2 as VideoFragment).setVideo(homeExTableClass.exVideo!!)
                }
                dialogExDetailBinding!!.pagerFragment.currentItem = 0


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    interface DialogDismissListener{

        fun onDismissListener(exerciseList: MutableList<HomeExTableClass>)
    }

    companion object {
        fun newInstance(
            item: HomePlanTableClass?,
            exerciseList: MutableList<HomeExTableClass>,
            mContext: Activity, fromEdit:Boolean = false,fromReplace:Boolean = false
        ): ExerciseDetailDialogFragment {
            return ExerciseDetailDialogFragment(item, exerciseList, mContext,fromEdit,fromReplace)
        }
    }
}