package com.loseweight

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.google.gson.Gson
import com.loseweight.databinding.ActivityAccessAllFeatureBinding
import com.loseweight.utils.Constant
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils


class AccessAllFeaturesActivity : BaseActivity() {

    var binding: ActivityAccessAllFeatureBinding? = null
    private var skuDetail: String = Constant.MONTHLY_SKU
    private var billingClient: BillingClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_access_all_feature)

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
        binding!!.handler = ClickHandler()

        initInAppPurchase()

    }


    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onBackClick() {
            finish()
        }

        fun onYearlyClick() {
            binding!!.llPerYear.backgroundTintList =
                ContextCompat.getColorStateList(this@AccessAllFeaturesActivity, R.color.primary)
            binding!!.tvYearlyTitle.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.primary
                )
            )
            binding!!.tvYearlyPrice.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.primary
                )
            )
            binding!!.imgCheckYearly.visibility = View.VISIBLE

            binding!!.llPerMonth.backgroundTintList =
                ContextCompat.getColorStateList(this@AccessAllFeaturesActivity, R.color.col_999)
            binding!!.tvMonthlyPrice.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.col_999
                )
            )
            binding!!.tvMonthlyTitle.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.col_999
                )
            )
            binding!!.imgCheckMonthly.visibility = View.GONE

            skuDetail = Constant.YEARLY_SKU
        }

        fun onPerMonthClick() {
            binding!!.llPerYear.backgroundTintList =
                ContextCompat.getColorStateList(this@AccessAllFeaturesActivity, R.color.col_999)
            binding!!.tvYearlyTitle.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.col_999
                )
            )
            binding!!.tvYearlyPrice.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.col_999
                )
            )
            binding!!.imgCheckYearly.visibility = View.GONE

            binding!!.llPerMonth.backgroundTintList =
                ContextCompat.getColorStateList(this@AccessAllFeaturesActivity, R.color.primary)
            binding!!.tvMonthlyPrice.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.primary
                )
            )
            binding!!.tvMonthlyTitle.setTextColor(
                ContextCompat.getColor(
                    this@AccessAllFeaturesActivity,
                    R.color.primary
                )
            )
            binding!!.imgCheckMonthly.visibility = View.VISIBLE
            skuDetail = Constant.MONTHLY_SKU


        }

        fun onContinueClick() {
            onPurchaseClick(skuDetail)
        }

    }


    /*private fun onPurchaseClick(SKU: String) {
        val skuList = arrayListOf<String>()
        skuList.add(SKU)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(params.build()) { _, list ->
            if(list.isNullOrEmpty().not())
            runOnUiThread {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(list!![0])
                    .build()
                val responseCode = billingClient!!.launchBillingFlow(this, billingFlowParams).responseCode
                Debug.e("BillingFlow responce", responseCode.toString() + "")
            }
        }
    }*/

    private fun onPurchaseClick(SKU: String) {
        val skuList = arrayListOf<String>()
        skuList.add(SKU)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(params.build()) { _, list ->
            if (list!!.isNotEmpty()) {
                runOnUiThread {
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(list[0])
                        .build()
                    val responseCode =
                        billingClient!!.launchBillingFlow(this, billingFlowParams).responseCode
                    Log.e("BillingFlow response", responseCode.toString() + "")
                }
            }
        }
    }


    private fun initInAppPurchase() {
        /*try {
            billingClient = BillingClient.newBuilder(this).setListener(purchaseUpdateListener).enablePendingPurchases().build()
            billingClient!!.startConnection(object : BillingClientStateListener {


                override fun onBillingServiceDisconnected() {
                }

                override fun onBillingSetupFinished(p0: BillingResult) {
                    checkSubscriptionList()
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }*/

        try {
            billingClient = BillingClient.newBuilder(this).setListener(purchaseUpdateListener)
                .enablePendingPurchases().build()
            billingClient!!.startConnection(object : BillingClientStateListener {


                override fun onBillingServiceDisconnected() {
                    Log.e("TAG", "onBillingServiceDisconnected::::: ")
                }

                override fun onBillingSetupFinished(p0: BillingResult) {
                    Log.e("TAG", "onBillingSetupFinished:::: " + p0.debugMessage)
                    checkSubscriptionList()
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

  /*  private val purchaseUpdateListener: PurchasesUpdatedListener = PurchasesUpdatedListener { result, purchase ->
        try {
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    Utils.setPref(this, Constant.PREF_KEY_PURCHASE_STATUS, true)

                }
                Debug.e("", "isFailure ,Error purchasing: $result")
            } else {
                Debug.e("", "Purchase successful.")
                Debug.e("", "FEATURES_PRO_KEY::${purchase!![0]?.sku}")
                Utils.setPref(this, Constant.PREF_KEY_PURCHASE_STATUS, true)
                val intent = Intent(getActivity(), SplashScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private val purchaseUpdateListener: PurchasesUpdatedListener =
        PurchasesUpdatedListener { result, _ ->
            try {
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                        Utils.setPref(this, Constant.PREF_KEY_PURCHASE_STATUS, true)
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    Utils.setPref(this, Constant.PREF_KEY_PURCHASE_STATUS, true)
                    val intent = Intent(getActivity(), SplashScreenActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                }
                checkSubscriptionList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



    /*private fun checkSubscriptionList() {
        if (billingClient != null) {
            //showDialog(mContext)
            var isPurchasedSku = false
            try {
                val purchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.SUBS)
                if (purchasesResult.responseCode == 0) {
                    val purchaseDataList = purchasesResult.purchasesList
                    Log.e("", "purchaseDataList::$purchaseDataList")
                    if (purchaseDataList != null) {
                        for (i in 0 until purchaseDataList.size) {
                            val purchaseData = purchaseDataList[i]
                            if ((purchaseData.sku == Constant.MONTHLY_SKU) || (purchaseData.sku == Constant.YEARLY_SKU)) {
                                isPurchasedSku = true
                            }
                        }
                    }
                    Debug.e("", " isPurchasedSku:: $isPurchasedSku")

                    Utils.setPref(this, Constant.PREF_KEY_PURCHASE_STATUS, isPurchasedSku)
                    getSKUDetails()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            dismissDialog()

        }
    }*/

    private fun checkSubscriptionList() {
        if (billingClient != null) {
            var isPurchasedSku = false
            try {
                billingClient!!.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                ) { purchasesResult, purchaseList ->
                    if (purchasesResult.responseCode == 0) {

                        if (purchaseList.isNotEmpty()) {
                            for (i in 0 until purchaseList.size) {
                                val purchaseData = purchaseList[i]
                                if ((purchaseData.products.contains(Constant.MONTHLY_SKU)) || (purchaseData.products.contains(Constant.YEARLY_SKU))) {
                                    isPurchasedSku = true
                                }

                                if (purchaseData.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    if (!purchaseData.isAcknowledged) {
                                        val acknowledgePurchaseParams =
                                            AcknowledgePurchaseParams.newBuilder()
                                                .setPurchaseToken(purchaseData.purchaseToken)
                                        billingClient!!.acknowledgePurchase(
                                            acknowledgePurchaseParams.build()
                                        ) { p0 ->
                                            Log.e("BillingResult ======>", p0.debugMessage)
                                        }
                                    }
                                }
                            }
                        }
                        Utils.setPref(this, Constant.PREF_KEY_PURCHASE_STATUS, isPurchasedSku)
                        getSKUDetails()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }


    /*private fun getSKUDetails() {

        val params = SkuDetailsParams.newBuilder()

        val productIds = arrayListOf<String>()
        productIds.add(Constant.MONTHLY_SKU)
        productIds.add(Constant.YEARLY_SKU)

        params.setSkusList(productIds).setType(BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == 0 && skuDetailsList != null) {
                for (thisResponse in skuDetailsList) {
                    try {
                        if (thisResponse.sku == Constant.MONTHLY_SKU) {
                            binding!!.tvMonthlyPrice.text = "${thisResponse.price} / Month"
                            Debug.e("","Monthly ::::::: ${Gson().toJson(thisResponse)}")
                        } else if (thisResponse.sku == Constant.YEARLY_SKU) {
                            Debug.e("","Yearly:::::::  ${thisResponse.price}")
                            binding!!.tvYearlyPrice.text = "${thisResponse.originalPrice} / Yearly"
                        }
                    } catch (e: java.lang.Exception) {
                        Debug.e("","Errrr:: $e")
                        e.printStackTrace()
                    }
                }
            } else {
                Debug.e("","ELSE:::::::BILLLLL ")
            }
//            dismissDialog()
        }
    }*/

    private fun getSKUDetails() {
        val productListMonth =
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(Constant.MONTHLY_SKU)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(Constant.YEARLY_SKU)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
            )

        val paramsNewMonth = QueryProductDetailsParams.newBuilder().setProductList(productListMonth)

        billingClient!!.queryProductDetailsAsync(paramsNewMonth.build()) { billingResult,
                                                                           skuDetailsList ->

            if (billingResult.responseCode == 0 && skuDetailsList.isNotEmpty()) {
                for (thisResponse in skuDetailsList) {
                    try {
                        runOnUiThread {
                            when (thisResponse.productId) {
                                Constant.MONTHLY_SKU -> {
                                    binding!!.tvMonthlyPrice.text = "${thisResponse.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice} / Month"
                                }
                                Constant.YEARLY_SKU -> {
                                    binding!!.tvYearlyPrice.text = "${thisResponse.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice} / Yearly"
                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

}
