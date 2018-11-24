package com.lazard.nyapp.nyapp

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    val baseJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + baseJob)
    val bgScope = CoroutineScope(Dispatchers.IO + baseJob)


    override fun onDestroy() {
        baseJob.cancel()
        super.onDestroy()

    }
}