package com.loseweight.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.loseweight.*
import com.loseweight.adapter.BodyFocusAdapter
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.databinding.FragmentMeBinding
import com.loseweight.databinding.FragmentPlanBinding
import com.loseweight.interfaces.DialogDismissListener
import com.loseweight.interfaces.SpinnerCallback
import com.loseweight.objects.HistoryDetailsClass
import com.loseweight.objects.Spinner
import com.loseweight.utils.Constant
import com.loseweight.utils.Utils
import com.loseweight.utils.watertracker.AlarmHelper
import java.util.*

class MeFragment : BaseFragment() {

    lateinit var binding: FragmentMeBinding
    var title: String? = null
    var segment: String? = null
    var rootContext:Context? = null
    val languageList = arrayListOf<Spinner>()

    companion object {
        fun newInstance(title: String, segment: String): MeFragment {
            val pane = MeFragment()
            val args = Bundle()
            /*args.putString(Constant.TITLE, title)
            args.putString(Constant.SEGMENT, segment)
            if (mNotificationDataModel != null) {
                args.putString(Constant.NotificationData, Gson().toJson(mNotificationDataModel))
            }*/
            pane.arguments = args
            return pane
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        rootContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false)
        /*if (arguments != null && arguments!!.getString(Constant.TITLE).isNullOrEmpty().not()) {
            title = arguments!!.getString(Constant.TITLE)
            Debug.e("title", title)
        }

        if (arguments != null && arguments!!.getString(Constant.SEGMENT).isNullOrEmpty().not()) {
            segment = arguments!!.getString(Constant.SEGMENT)
            Debug.e("segment", segment)
        }

        if (arguments != null && arguments!!.getString(Constant.NotificationData).isNullOrEmpty().not()) {
            var notificationData = arguments!!.getString(Constant.NotificationData)
            mNotificationDataModel = Gson().fromJson(JSONObject(notificationData).toString(), object :
                    TypeToken<NotificationDataModel>() {}.type)!!
            Debug.e("notificationData", notificationData)
        }*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    private fun init() {
        binding.handler = ClickHandler()

        languageList.add(Spinner("en", "English", Utils.getSelectedLanguage(context).equals("en")))
        languageList.add(Spinner("es", "Española", Utils.getSelectedLanguage(context).equals("es")))

        binding.SwitchTurnOnWater.setOnCheckedChangeListener { buttonView, isChecked ->
            Utils.setPref(context, Constant.PREF_IS_WATER_TRACKER_ON, isChecked)
            if(isChecked)
            {
                Utils.setPref(context, Constant.PREF_IS_WATER_TRACKER_ON, true)

                AlarmHelper.setNotificationsAlarm(context)
                AlarmHelper.setCancelNotificationAlarm(context)
            }else{
                AlarmHelper.setCancelNotificationAlarm(context)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if(Utils.isPurchased(context))
            binding.llGoPremium.visibility = View.GONE
        else
            binding.llGoPremium.visibility = View.VISIBLE

        if(getContext() is HomeActivity)
        (context as HomeActivity).fillWaterTracker()
        binding.SwitchTurnOnWater.isChecked =  Utils.getPref(context, Constant.PREF_IS_WATER_TRACKER_ON, true)
    }

    inner class ClickHandler {

        fun onSoundOptionClick() {
            (rootContext as BaseActivity).showSoundOptionDialog(rootContext!!,object :
                DialogDismissListener {
                override fun onDialogDismiss() {

                }

            })
        }

        fun onGoPremiumClick(){
            val intent = Intent(rootContext,AccessAllFeaturesActivity::class.java)
            startActivity(intent)
        }

        fun onMyProfileClick(){
            val intent = Intent(rootContext,MyProfileActivity::class.java)
            startActivity(intent)
        }

        fun onRestartProgressClick(){
            showRestartProgressConfirmationDialog()
        }

        fun onAddReminderClick(){
            val intent = Intent(rootContext,ReminderActivity::class.java)
            startActivity(intent)
        }

        fun onVoiceOptionClick(){
            val intent = Intent(rootContext,VoiceOptionActivity::class.java)
            startActivity(intent)
        }

        fun onRatUsClick(){
            Utils.rateUs(rootContext!!)
        }

        fun onFeedBackClick(){
            Utils.contactUs(rootContext!!)
        }

        fun onPrivacyPolicyClick()
        {
            Utils.openUrl(rootContext!!, getString(R.string.privacy_policy_link))
        }

        fun onCommonQuestionClick()
        {
            val intent = Intent(rootContext,CommonQuestionActivity::class.java)
            startActivity(intent)
        }

        fun onShareWithFriendClick()
        {
            val link = "https://play.google.com/store/apps/details?id=${rootContext!!.packageName}"
            val strSubject = ""
            val strText = "I'm exercise with ${getString(R.string.app_name)}, it's so useful! All kinds of Lose weight routines here for workout, running, pain relief, etc. It must have one you need, try it for free!" +
                    "\n\n" +
                    "Download the app:$link"

            Utils.shareStringLink(rootContext!!, strSubject, strText)
        }

        fun onChangeLanguageClick(){
            Utils.showSpinner(
                context,
                getString(R.string.change_language),
                null,
                languageList,
                false,
                false,
                object : SpinnerCallback {
                    override fun onDone(list: ArrayList<Spinner>) {

                        val item = if(list.isNotEmpty())
                            list[0]
                        else
                            languageList[0]
                        Utils.setPref(context, Constant.PREF_LANGUAGE, item.ID)
                        Utils.setPref(context, Constant.PREF_LANGUAGE_NAME, item.title)
                        (context as Activity).finish()
                        startActivity(Intent(context, HomeActivity::class.java))
                        (context as Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                })
        }

    }

    private fun showRestartProgressConfirmationDialog() {
        val builder = AlertDialog.Builder(rootContext!!, R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.restart_confirmation_msg))
        builder.setPositiveButton(R.string.restart) { dialog, which ->
            dbHelper.restartProgress()
            LocalBroadcastManager.getInstance(rootContext!!).sendBroadcast(Intent(Constant.FINISH_ACTIVITY))
            val i = Intent(rootContext!!,HomeActivity::class.java)
            startActivity(i)
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()
    }
}