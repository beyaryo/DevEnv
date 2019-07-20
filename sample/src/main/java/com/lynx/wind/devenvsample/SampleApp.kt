package com.lynx.wind.devenvsample

import android.app.Application
import com.lynx.wind.dev.DevEnv

class SampleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DevEnv(this, BuildConfig.APPLICATION_ID, BuildConfig.DEBUG)
            .setDefaultUrl("http://btn.vasdev.co.id/")
            .setLogEnabled(true)
            .setCustomSetting("image", "http://image.upload.url")
            .setCustomSetting("giant", "oyi")
            .build()
    }
}