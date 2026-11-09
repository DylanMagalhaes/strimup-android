package com.strimup.feature.cuts.presentation.component

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext

private const val MIN_PRELOAD_BANDWIDTH_KBPS = 2_000

@Composable
fun rememberCanPreloadVideos(): Boolean {
    val context = LocalContext.current
    val canPreload by produceState(initialValue = context.canPreloadVideos(), context) {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                value = connectivityManager.canPreloadVideos(capabilities)
            }

            override fun onLost(network: Network) {
                value = false
            }
        }
        connectivityManager?.registerDefaultNetworkCallback(callback)
        awaitDispose { connectivityManager?.unregisterNetworkCallback(callback) }
    }
    return canPreload
}

private fun Context.canPreloadVideos(): Boolean {
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    val capabilities = connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
    return capabilities != null && connectivityManager.canPreloadVideos(capabilities)
}

private fun ConnectivityManager?.canPreloadVideos(capabilities: NetworkCapabilities): Boolean {
    val isDataSaverOn = this?.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED &&
        !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    val isFastEnough = capabilities.linkDownstreamBandwidthKbps >= MIN_PRELOAD_BANDWIDTH_KBPS
    return !isDataSaverOn && isFastEnough
}
