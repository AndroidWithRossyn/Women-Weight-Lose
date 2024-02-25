package com.loseweight.utils
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.loseweight.R


class RetrofitProgressDialog : Thread {

    internal var pd: Dialog? = null
    internal var context: Activity

    val isShowing: Boolean
        get() {
            try {
                return pd!!.isShowing
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    constructor(context: Activity) {
        this.context = context
        pd = Dialog(context)
        pd!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        // pd.setContentView(R.layout.custom_progress);
        pd!!.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        pd!!.setContentView(
                context.layoutInflater.inflate(R.layout.custom_progress, null),
                ViewGroup.LayoutParams(
                    Utils.getDeviceWidth(context),
                    Utils.getDeviceHeight(context)
                ))
    }

    constructor(context: Activity, msg: String) {
        this.context = context
        pd = ProgressDialog(context)
        pd!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        // pd.setContentView(R.layout.custom_progress);
        pd!!.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))

        pd!!.setContentView(
                context.layoutInflater.inflate(R.layout.custom_progress, null),
                ViewGroup.LayoutParams(
                    Utils.getDeviceWidth(context),
                    Utils.getDeviceHeight(context)
                ))
    }

    override fun run() {
        try {

            // preparing a looper on current thread
            // the current thread is being detected implicitly
            Looper.prepare()

            context.runOnUiThread { pd!!.show() }

            Looper.loop()
            // Thread will start

        } catch (t: Exception) {
            t.printStackTrace()
        }

    }

    fun show(message: String) {
        try {
            this.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Synchronized
    fun dismiss() {
        try {

            context.runOnUiThread {
                if (pd != null && pd!!.isShowing) {
                    pd!!.dismiss()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Synchronized
    fun setMessage(message: String) {

        // Wrap DownloadTask into another Runnable to track the statistics
        try {
            if (pd != null) {

                context.runOnUiThread {
                    // pd.setMessage(message);
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        fun getInstant(context: Activity): RetrofitProgressDialog {
            return RetrofitProgressDialog(context)
        }

        fun getInstant(context: Activity, msg: String): RetrofitProgressDialog {
            return RetrofitProgressDialog(context, msg)
        }
    }

}
