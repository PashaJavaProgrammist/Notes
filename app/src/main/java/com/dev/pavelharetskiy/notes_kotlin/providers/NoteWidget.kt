package com.dev.pavelharetskiy.notes_kotlin.providers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.activities.CreateActivity
import com.dev.pavelharetskiy.notes_kotlin.requestCodeW


class NoteWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for ((i, value) in appWidgetIds.withIndex()) {
            val appWidgetId = appWidgetIds[i]

            val crIntent = Intent(context, CreateActivity::class.java)
            val pIntent = PendingIntent.getActivity(context, requestCodeW, crIntent, 0)

            val widgetView = RemoteViews(context.packageName, R.layout.widget)
            widgetView.setOnClickPendingIntent(R.id.wd_add, pIntent)

            appWidgetManager.updateAppWidget(appWidgetId, widgetView)
        }
    }

}