package com.lazard.nyapp.nyapp.ui

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.util.StatsLog.logEvent
import android.R.attr.name
import android.os.Bundle



open class BaseActivity : AppCompatActivity() {
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    val baseJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + baseJob)
    val bgScope = CoroutineScope(Dispatchers.IO + baseJob)


    override fun onDestroy() {
        baseJob.cancel()
        super.onDestroy()

    }

    fun logAnalytics(id:String,name:String,category:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, category)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
}