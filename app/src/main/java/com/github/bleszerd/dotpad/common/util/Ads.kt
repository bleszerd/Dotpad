package com.github.bleszerd.dotpad.common.util

import android.content.Context
import com.google.android.gms.ads.AdRequest
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

    init {
        if (adsEnabled){
            MobileAds.initialize(context)
            isInitialized = true
        }
    }

    fun buildAd(): AdRequest {
        return AdRequest.Builder().build()
    }
}