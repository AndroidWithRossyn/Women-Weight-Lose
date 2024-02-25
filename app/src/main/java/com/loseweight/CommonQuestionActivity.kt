package com.loseweight

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loseweight.adapter.CommonQuestionExpandableAdapter
import com.loseweight.adapter.CommonQuestionTitleAdapter
import com.loseweight.databinding.ActivityCommonQuestionBinding
import com.loseweight.interfaces.TopBarClickListener
import com.loseweight.objects.ReminderTableClass
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import com.stretching.objects.CommonQuestionClass
import com.stretching.objects.CommonQuestionDataClass
import java.util.*


class CommonQuestionActivity : BaseActivity() {

    var binding: ActivityCommonQuestionBinding? = null
    var commonQuestionTitleAdapter: CommonQuestionTitleAdapter? = null
    var commonQuestionExpandableAdapter: CommonQuestionExpandableAdapter? = null
    var questionLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_common_question)

        loadBannerAd(binding!!.llAdView,binding!!.llAdViewFacebook)
        initIntentParam()
        initBack()
        init()
    }

    private fun initIntentParam() {
        try {
            /*if (intent.extras != null) {
                if (intent.extras!!.containsKey("from")) {
                    val from = intent.getStringExtra("from")
                    if(from.equals(Constant.FROM_SETTING))
                    {
                        initBack()
                        binding!!.topbar.isBackShow = true
                        return
                    }
                }
            }
            binding!!.topbar.isMenuShow = true*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.tvTitleText.text = getString(R.string.common_questions)
        binding!!.topbar.isBackShow = true
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        commonQuestionTitleAdapter = CommonQuestionTitleAdapter(this)
        binding!!.rvTitle.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding!!.rvTitle.setAdapter(commonQuestionTitleAdapter)

        commonQuestionTitleAdapter!!.setEventListener(object :
            CommonQuestionTitleAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = commonQuestionTitleAdapter!!.getItem(position)
                commonQuestionTitleAdapter!!.setSelection(position)

                if (position == 1)
                    questionLayoutManager!!.scrollToPositionWithOffset(4, 0)
                else if (position == 2)
                    questionLayoutManager!!.scrollToPositionWithOffset(8, 0)
                else
                    questionLayoutManager!!.scrollToPositionWithOffset(position, 0)

                /*if(position == 0)
                    binding!!.nestedScrollView.smoothScrollTo(0,0)
                else if (position == 1)
                    binding!!.nestedScrollView.smoothScrollTo(0,700)
                else if (position == 2)
                    binding!!.nestedScrollView.smoothScrollTo(0,1400)*/
            }
        })

        commonQuestionExpandableAdapter = CommonQuestionExpandableAdapter(this, arrayListOf())
        questionLayoutManager = LinearLayoutManager(this)
        binding!!.rvQuestions.layoutManager = questionLayoutManager
        binding!!.rvQuestions.setAdapter(commonQuestionExpandableAdapter)

        commonQuestionExpandableAdapter!!.setEventListener(object :
            CommonQuestionExpandableAdapter.EventListener {

            override fun OnMenuClick(parentPosition: Int, childPosition: Int) {

            }

            override fun OnMoreClick(parentPosition: Int, childPosition: Int, view: View) {

            }
        })

        /*binding!!.nestedScrollView.setOnScrollChangeListener(object :NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                Debug.e("scrollY ====>", "$scrollY")
                if(scrollY < 600){
                    commonQuestionTitleAdapter!!.setSelection(0)
                }else if(scrollY < 1250){
                    commonQuestionTitleAdapter!!.setSelection(1)
                }else if(scrollY > 1250){
                    commonQuestionTitleAdapter!!.setSelection(2)
                }
            }

        })*/

        binding!!.rvQuestions.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(questionLayoutManager!!.findFirstCompletelyVisibleItemPosition() < 4){
                    commonQuestionTitleAdapter!!.setSelection(0)
                }else if(questionLayoutManager!!.findFirstCompletelyVisibleItemPosition() < 7){
                    commonQuestionTitleAdapter!!.setSelection(1)
                }else if(questionLayoutManager!!.findFirstCompletelyVisibleItemPosition() > 7){
                    commonQuestionTitleAdapter!!.setSelection(2)
                }
            }
        })


        fillData()

    }


    private fun fillData() {
        try {
            var quetions = arrayListOf<CommonQuestionDataClass>()

            /////////////////////////////////////////
            ////////////// APP Question ////////////
            var questionTitle = CommonQuestionDataClass()
            questionTitle.title = "App"
            questionTitle.isSelected = true
            questionTitle.arrQuestionDetail = arrayListOf()

            var question = CommonQuestionClass()
            question.question = "How often should i exercise?"
            question.answer =
                "You can repeat your daily workouts as you need. For beginners, we suggest that you exercise once a day, 2-3 times a week. For experienced users, you can exercise once or several times a day as you need. \n" +
                        "\n" +
                        "All workouts are designed by professionals, it's suitable for everyone, teens and adults, men and women. \n" +
                        "\n" +
                        "If you're ill, injured, or unable to exercise due to your personal physical condition, please consult your doctor before exercising. We're not responsible for any injuries you may sustain while exercising. "

            questionTitle.arrQuestionDetail.add(question)

            var question2 = CommonQuestionClass()
            question2.question = "I'm a beginner. Where to start?"
            question2.answer =
                "As a beginner, your goal should be to adapt your body to your workout, and make workout a daily habit. The exercise intensity increases step-by-step, you'll find it's easy to pick up. \n" +
                        "\n" +
                        "Besides the 30-day plan, you can also try the training for beginners in the Plan page. With animation and video guidance, you can make sure you use the right form during every exercise. "
            questionTitle.arrQuestionDetail.add(question2)


            var question3 = CommonQuestionClass()
            question3.question = "Should I warm up & cool down?"
            question3.answer =
                "Yes. Warm-up is important before your workout. It helps you reduce injuries and improve your workout performance. \n" +
                        "\n" +
                        "Cooling down after workout is also necessary, because it helps your body return to its normal state and even reduce the chance of muscle soreness. "
            questionTitle.arrQuestionDetail.add(question3)



            quetions.add(questionTitle)


            /////////////////////////////////////////
            //////////// WorkOut Question //////////

            var questionTitle2 = CommonQuestionDataClass()
            questionTitle2.title = "WorkOut"
            questionTitle2.arrQuestionDetail = arrayListOf()

            var wQuestion = CommonQuestionClass()
            wQuestion.question = "I feel not well."
            wQuestion.answer =
                "If any discomfort occurs during exercise, please stop exercising immediately and go to the doctor. "

            questionTitle2.arrQuestionDetail.add(wQuestion)

            var wQuestion2 = CommonQuestionClass()
            wQuestion2.question = "I've finished 30 days training. What now?"
            wQuestion2.answer =
                "Good job! You can repeat the 30 days train-ing to reinforce your results, or find more training that interests you in the Plan Page. "

            questionTitle2.arrQuestionDetail.add(wQuestion2)

            var wQuestion3 = CommonQuestionClass()
            wQuestion3.question = "Can't see the results?"
            wQuestion3.answer =
                "This app will help you lose weight safely. You can't lose weight overnight. \n" +
                        "\n" +
                        "Since losing weight is a long-term process, you need to stick to it."

            questionTitle2.arrQuestionDetail.add(wQuestion3)


            quetions.add(questionTitle2)


            /////////////////////////////////////////
            //////////// Payment Question //////////

            var questionTitle3 = CommonQuestionDataClass()
            questionTitle3.title = "Payment"
            questionTitle3.arrQuestionDetail = arrayListOf()

            var pQuestion = CommonQuestionClass()
            pQuestion.question = "How to continue using Premium version on my new device?"
            pQuestion.answer =
                "1. Make sure the app on your new device is downloaded from Google Play. \n" +
                        "\n" +
                        "2. Make sure the account you're using on Google Play is the same as the one you used to subscribe. \n" +
                        "\n" +
                        "3. Restart the app, it'll auto revert to your premium version. "

            questionTitle3.arrQuestionDetail.add(pQuestion)

            var pQuestion2 = CommonQuestionClass()
            pQuestion2.question = "How to cancel the subscription?"
            pQuestion2.answer =
                "Google Play -> Subscriptions -> Find Lose Weight App for Women -> Cancel Subscription "

            questionTitle3.arrQuestionDetail.add(pQuestion2)

            var pQuestion3 = CommonQuestionClass()
            pQuestion3.question = "How was charged, but no notification!"
            pQuestion3.answer =
                "If you don't cancel the subscription, your ac-count will be charged for renewal within 24 hours prior to the end of the current period.\n" +
                        " \n" +
                        "Please check the subscription page of the Play Store to find out your subscription status. "

            questionTitle3.arrQuestionDetail.add(pQuestion3)

            var pQuestion4 = CommonQuestionClass()
            pQuestion4.question = "Can I get a refund?"
            pQuestion4.answer =
                "All payments made through Google Play are controlled and managed by Google. \n" +
                        "\n" +
                        "If you forgot to cancel the subscription or subscribed by mistake, please contact Google Play for more information. "

            questionTitle3.arrQuestionDetail.add(pQuestion4)

            var pQuestion5 = CommonQuestionClass()
            pQuestion5.question = "Uninstalled app, but still be charged?"
            pQuestion5.answer =
                "If you uninstalled the app, but don't cancel the subscription, your account will still be charged by Google Play. "

            questionTitle3.arrQuestionDetail.add(pQuestion5)

            quetions.add(questionTitle3)

            commonQuestionTitleAdapter!!.addAll(quetions)
            commonQuestionExpandableAdapter!!.addAll(quetions)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onSendFeedBackClick() {
            Utils.contactUs(this@CommonQuestionActivity)
        }
    }


    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                finish()
                setResult(Activity.RESULT_OK)
            }

        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

}
