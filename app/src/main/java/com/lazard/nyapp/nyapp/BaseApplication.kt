package com.lazard.nyapp.nyapp

import android.app.Application

class BaseApplication : Application() {
    companion object {
        var instance : BaseApplication? = null
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}