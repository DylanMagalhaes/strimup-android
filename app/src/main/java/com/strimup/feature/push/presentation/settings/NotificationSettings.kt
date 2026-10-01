package com.strimup.feature.push.presentation.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect

fun Context.canPostNotifications(): Boolean {
    val isPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    return isPermissionGranted && NotificationManagerCompat.from(this).areNotificationsEnabled()
}

fun Context.openAppNotificationSettings() {
    val notificationSettings = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

    try {
        startActivity(notificationSettings)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData("package:$packageName".toUri())
        )
    }
}

@Composable
fun rememberNotificationsEnabled(): Boolean {
    val context = LocalContext.current
    var areNotificationsEnabled by remember { mutableStateOf(context.canPostNotifications()) }

    LifecycleResumeEffect(context) {
        areNotificationsEnabled = context.canPostNotifications()
        onPauseOrDispose {}
    }

    return areNotificationsEnabled
}
