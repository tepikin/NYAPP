package com.lazard.nyapp.nyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.services.settings.IconRequest.build
import android.provider.Settings.Secure
import android.util.Log


open class BaseActivityImpl : AppCompatActivity() {
    val adView : AdView? by lazy { findViewById<AdView?>(com.lazard.nyapp.nyapp.R.id.adView) }

    override fun onPostCreate(savedInstanceState: Bundle?){
        super.onPostCreate(savedInstanceState)
        adView?:return

        MobileAds.initialize(this, resources.getString(com.lazard.nyapp.nyapp.R.string.admob_applicationId))

//        val android_id = Settings.Secure.getString(
//            getContentResolver(),
//            Settings.Secure.ANDROID_ID
//        )
//        Log.e("android_id",""+android_id)

        val adRequest = AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("E9E948CD0C00C42A7339DD77DCF5A333")//samsung test device
            .addTestDevice("941CD49D83E7670F3F20CADD8C8F8A0B")//samsung test device
            .addTestDevice("08F3F2B9E02040A8BD46ADF3582F7758")//honor test device
            .build()
        adView?.loadAd(adRequest)
    }


}