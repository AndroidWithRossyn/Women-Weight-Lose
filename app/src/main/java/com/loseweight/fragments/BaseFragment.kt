package com.loseweight.fragments

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import com.loseweight.utils.Constant
import com.loseweight.utils.RetrofitProgressDialog
import com.loseweight.utils.Utils
import com.utillity.db.DataHelper
import java.util.*


open class BaseFragment : Fragment() {

    internal lateinit var toast: Toast
    internal lateinit var context: Context
    lateinit var dbHelper: DataHelper

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toast = Toast.makeText(activity, "", Toast.LENGTH_LONG)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dbHelper = DataHelper(context)
        this.context = context
    }


    fun showToast(text: String, duration: Int) {
        (context as Activity).runOnUiThread {
            toast.setText(text)
            toast.duration = duration
            toast.show()
        }
    }

    fun showToast(text: String) {
        (context as Activity).runOnUiThread {
            toast.setText(text)
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    internal var ad: RetrofitProgressDialog? = null

    fun showDialog(msg: String) {
        try {
            if (ad != null && ad!!.isShowing) {
                return
            }
            ad = RetrofitProgressDialog.getInstant(context as Activity)
            ad!!.show(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setMessage(msg: String) {
        try {
            ad!!.setMessage(msg)
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



}
