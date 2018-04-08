package com.dev.pavelharetskiy.notes_kotlin

import android.app.Application
import com.crashlytics.android.answers.Answers
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import io.fabric.sdk.android.Fabric


class NotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Answers())
        FlowManager.init(FlowConfig.Builder(this).build())
    }

    override fun onTerminate() {
        FlowManager.destroy()
        super.onTerminate()
    }
}