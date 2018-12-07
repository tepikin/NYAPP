package com.lazard.nyapp.nyapp

import androidx.multidex.MultiDexApplication

class BaseApplication : MultiDexApplication() {
    companion object {
        var instance : BaseApplication? = null
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}