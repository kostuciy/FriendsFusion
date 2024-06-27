package com.kostuciy.friendsfusion.presentation

import android.app.Application
import com.google.android.material.color.DynamicColors

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}