package com.strimup

import android.app.Application
import com.strimup.feature.push.presentation.PushNotificationChannels
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StrimupApplication : Application() {

    @Inject
    lateinit var pushNotificationChannels: PushNotificationChannels

    override fun onCreate() {
        super.onCreate()
        pushNotificationChannels.create()
    }
}