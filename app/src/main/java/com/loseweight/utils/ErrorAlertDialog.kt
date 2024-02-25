package com.loseweight.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.loseweight.BaseActivity
import com.loseweight.R

class ErrorAlertDialog {

    private var isYesShow: Boolean = false
    private var isNoShow: Boolean = false

    private var positiveButtonLabel: String? = null
    private var negativeButtonLabel: String? = null

    private var context: Context? = null

    var alertDialog: AlertDialog? = null

    private lateinit var title: String
    private lateinit var message: String

    lateinit var dialogButtonClick: DialogButtonClick

    constructor(context: Context) {
        this.context = context

    }

    fun setTitle(title: String): ErrorAlertDialog {
        this.title = title
        return this
    }

    fun setMessage(message: String): ErrorAlertDialog {
        this.message = message
        return this
    }

    fun setPositiveButton(positiveButtonLabel: String): ErrorAlertDialog {
        isYesShow = true
        this.positiveButtonLabel = positiveButtonLabel
        return this
    }

    fun setNegativeButton(negativeButtonLabel: String): ErrorAlertDialog {
        isNoShow = true
        this.negativeButtonLabel = negativeButtonLabel
        return this
    }

    fun setOnButtonClickListener(dialogButtonClick: DialogButtonClick): ErrorAlertDialog {
        this.dialogButtonClick = dialogButtonClick
        return this
    }

    fun show() {

        try {
            val pd: AlertDialog.Builder = AlertDialog.Builder((context as BaseActivity), R.style.MyAlertDialogStyle)
            pd.setTitle(title)
            pd.setMessage(message)
            if (isNoShow) {
                pd.setNegativeButton(negativeButtonLabel){dialog,which ->
                    alertDialog!!.dismiss()
                    dialogButtonClick.onNegativeClick()
                }
            }
            if (isYesShow) {
                pd.setPositiveButton(positiveButtonLabel){dialog,which ->
                    alertDialog!!.dismiss()
                    dialogButtonClick.onPositiveClick()
                }
            }

            alertDialog = pd.create()
            alertDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface DialogButtonClick {
        fun onPositiveClick()
        fun onNegativeClick()
    }
}