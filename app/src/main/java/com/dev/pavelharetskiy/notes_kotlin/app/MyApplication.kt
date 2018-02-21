package com.dev.pavelharetskiy.notes_kotlin.app

import android.app.Application
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FlowManager.init(FlowConfig.Builder(this).build())
    }

    override fun onTerminate() {
        FlowManager.destroy()
        super.onTerminate()
    }
}