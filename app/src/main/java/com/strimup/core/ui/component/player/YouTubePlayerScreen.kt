package com.strimup.core.ui.component.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun YouTubePlayerScreen(
    videoId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false,
) {
    val host = rememberYouTubePlayerHost(videoId = videoId, isVertical = isVertical)

    ImmersiveModeEffect()
    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        AndroidView(
            factory = { host.webView },
            modifier = Modifier.fillMaxSize(),
        )

        YouTubePlayerControls(
            state = host.state,
            onClose = onBack,
        )
    }
}
