package com.dev.pavelharetskiy.notes_kotlin

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import io.fabric.sdk.android.Fabric

class NotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Answers())

        // Set up Crashlytics, disabled for debug builds
        val crashLyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()

        Fabric.with(this, crashLyticsKit)

        FlowManager.init(FlowConfig.Builder(this).build())
    }

    override fun onTerminate() {
        FlowManager.destroy()
        super.onTerminate()
    }
}