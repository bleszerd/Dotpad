package com.github.bleszerd.dotpad.common.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds

/**
Dotpad
19/08/2021 - 09:16
Created by bleszerd.
@author alive2k@programmer.net
 */
class Ads(context: Context) {
    private var isInitialized = false
    private var adsEnabled = true

    companion object {
        const val BANNER_BOTTOM_HOMENOTE_UNIT_ID = "ca-app-pub-7210657085238164/1016712084"
        const val BANNER_BOTTOM_EDITNOTE_UNIT_ID = "ca-app-pub-7210657085238164/6176898798"
    }

    init {
        if (adsEnabled) {
            MobileAds.initialize(context)
            isInitialized = true
        }
    }

    fun buildAd(): AdRequest {
        return AdRequest.Builder().build()
    }

    fun getAdSize(context: Context, wm: WindowManager, adHost: View): AdSize {
        val adSize: AdSize
        val display = wm.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adHost.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
}