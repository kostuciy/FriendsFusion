package com.kostuciy.friendsfusion.core.presentation

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.kostuciy.data.vk.TokenHandler
import com.vk.api.sdk.VK
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppApplication : Application() {
    @Inject lateinit var vkTokenHandler: TokenHandler

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        VK.addTokenExpiredHandler(vkTokenHandler)
    }
}
