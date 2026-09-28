package com.strimup.core.ui.browser

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.openInCustomTab(url: String): Boolean {
    return try {
        CustomTabsIntent.Builder().build().launchUrl(this, url.toUri())
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
