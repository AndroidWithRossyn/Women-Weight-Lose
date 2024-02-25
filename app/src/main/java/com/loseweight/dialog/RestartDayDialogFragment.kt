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

import com.loseweight.R
import com.loseweight.databinding.BottomSheetRestartConfBinding


class RestartDayDialogFragment(
    val mContext: BaseActivity
) : DialogFragment() {

    var dialogRestartDayBinding: BottomSheetRestartConfBinding? = null
    var dialogDismissListener:DialogDismissListener?= null
    var needToRestart = false

    init {
        dialogRestartDayBinding = DataBindingUtil.inflate(
            mContext.layoutInflater,
            R.layout.bottom_sheet_restart_conf,
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

        return dialogRestartDayBinding!!.root
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
        dialogRestartDayBinding!!.btnCancel.setOnClickListener {
            needToRestart = false
            dismiss()
        }

        dialogRestartDayBinding!!.btnOk.setOnClickListener {
            needToRestart = true
            dismiss()
        }

        dialogRestartDayBinding!!.imgClose.setOnClickListener {
            needToRestart = true
            dismiss()
        }
    }


    fun show(fragmentManager: FragmentManager) {
        fragmentManager.executePendingTransactions()
        this.show(fragmentManager, "")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissListener?.onDismissListener(needToRestart)
    }

    fun setOnEventListener(listener:DialogDismissListener){
        dialogDismissListener = listener
    }

    private fun fillData(b: Boolean) {


    }

    interface DialogDismissListener{

        fun onDismissListener(needToClear: Boolean)
    }

    companion object {
        fun newInstance(
            mContext: BaseActivity
        ): RestartDayDialogFragment {
            return RestartDayDialogFragment(mContext)
        }
    }
}