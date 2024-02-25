package com.loseweight.facebookad

import android.content.Context
import android.util.Log
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.loseweight.BuildConfig.DEBUG

/** Sample class that shows how to call initialize() method of Audience Network SDK. */
class AudienceNetworkInitializeHelper : AudienceNetworkAds.InitListener {

  override fun onInitialized(result: AudienceNetworkAds.InitResult) {
    Log.d(AudienceNetworkAds.TAG, result.message)
  }

  companion object {

    /**
     * It's recommended to call this method from Application.onCreate(). Otherwise you can call it
     * from all Activity.onCreate() methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
    internal fun initialize(context: Context) {

      if (!AudienceNetworkAds.isInitialized(context)) {
        if (DEBUG) {
          AdSettings.turnOnSDKDebugger(context)
        }

        AudienceNetworkAds.buildInitSettings(context)
          .withInitListener(AudienceNetworkInitializeHelper())
          .initialize()
      }
    }
  }
}
