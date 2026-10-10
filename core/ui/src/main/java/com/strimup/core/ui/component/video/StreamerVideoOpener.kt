package com.strimup.core.ui.component.video

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source
import com.strimup.core.ui.browser.ExternalLinkOpener
import com.strimup.core.ui.browser.rememberExternalLinkOpener

@Stable
class StreamerVideoOpener(private val openExternalLink: ExternalLinkOpener) {
    var playingVideo by mutableStateOf<VideoSource.HostedFile?>(null)
        private set

    var playingTitle by mutableStateOf("")
        private set

    fun open(video: Streamer.Video) {
        when (val source = video.source()) {
            is VideoSource.HostedFile -> {
                playingTitle = video.title
                playingVideo = source
            }

            is VideoSource.ExternalLink -> openExternalLink(source.url)
            VideoSource.Unavailable -> Unit
        }
    }

    fun close() {
        playingVideo = null
    }
}

@Composable
fun rememberStreamerVideoOpener(snackBarHostState: SnackbarHostState): StreamerVideoOpener {
    val openExternalLink = rememberExternalLinkOpener(snackBarHostState)
    return remember(openExternalLink) { StreamerVideoOpener(openExternalLink) }
}

@Composable
fun StreamerVideoPlayerHost(opener: StreamerVideoOpener) {
    opener.playingVideo?.let { video ->
        HostedVideoPlayerDialog(
            url = video.url,
            title = opener.playingTitle,
            onDismiss = opener::close,
        )
    }
}
