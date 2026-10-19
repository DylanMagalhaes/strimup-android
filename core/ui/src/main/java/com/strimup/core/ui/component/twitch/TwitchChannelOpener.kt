package com.strimup.core.ui.component.twitch

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.net.toUri
import com.strimup.core.ui.R
import kotlinx.coroutines.launch

fun interface TwitchChannelOpener {
    operator fun invoke(channel: String)
}

@Composable
fun rememberTwitchChannelOpener(snackBarHostState: SnackbarHostState): TwitchChannelOpener {
    val context = LocalContext.current
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()

    return remember(context, resources, snackBarHostState, coroutineScope) {
        TwitchChannelOpener { channel ->
            if (!context.openTwitchChannel(channel)) {
                coroutineScope.launch { snackBarHostState.showSnackbar(resources.getString(R.string.error_open_link)) }
            }
        }
    }
}

private fun Context.openTwitchChannel(channel: String): Boolean =
    startView("twitch://stream/$channel") || startView("https://www.twitch.tv/$channel")

private fun Context.startView(uri: String): Boolean = try {
    startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
    true
} catch (_: ActivityNotFoundException) {
    false
}
