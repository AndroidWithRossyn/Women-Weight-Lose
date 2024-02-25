package com.loseweight

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.loseweight.databinding.*
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.utils.Utils


class VoiceOptionActivity : BaseActivity() {

    var binding: ActivityVoiceOptionsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_options)

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
        binding!!.topbar.tvTitleText.text = getString(R.string.voice_options_tts)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()


    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onTestVoiceClick(){
            MyApplication.speechText(this@VoiceOptionActivity, getString(R.string.did_you_hear_test_voice),true)
            Thread.sleep(1000)
            showVoiceConfirmationDialog()
        }

        fun onSelectTTsEngineClick(){
            showSelectTTSEngineDialog()
        }

        fun onDownloadTTsEngineClick(){
            Utils.DownloadTTSEngine(this@VoiceOptionActivity)
        }

        fun onDeviceTTSSettingClick(){
            val intent = Intent("com.android.settings.TTS_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }


    private fun showVoiceConfirmationDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        var dialog : Dialog
        builder.setMessage(R.string.did_you_hear_test_voice)
        builder.setPositiveButton(R.string.yes) { dialog, which ->
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no, { dialog, which ->
            dialog.dismiss()
            showTestVoiceFailDialog()
        })
        dialog =  builder.create()
        dialog.show()
    }

    private fun showTestVoiceFailDialog() {

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        var dialog : Dialog? = null
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_test_voice_fail, null)
        val dialogBinding: DialogTestVoiceFailBinding? = DataBindingUtil.bind(v)
        builder.setView(v)

        dialogBinding!!.tvDownloadTTSEngine.setOnClickListener {
            Utils.DownloadTTSEngine(this)
            dialog!!.dismiss()
        }

        dialogBinding.tvSelectTTSEngine.setOnClickListener {
            showSelectTTSEngineDialog()
            dialog!!.dismiss()
        }

        builder.setView(v)
        dialog =  builder.create()
        dialog.show()
    }

    private fun showSelectTTSEngineDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        var dialog : Dialog? = null
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_select_tts_engine, null)
        builder.setView(v)
        builder.setTitle(getString(R.string.please_choose_guide_voice_engine))
        val dialogBinding: DialogSelectTtsEngineBinding? = DataBindingUtil.bind(v)

        dialogBinding!!.llContainer.setOnClickListener {
            dialog!!.dismiss()
        }

        builder.setView(v)
        dialog =  builder.create()
        dialog.show()

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
