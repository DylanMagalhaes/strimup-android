package com.strimup.core.ui.component.video

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source
import com.strimup.core.ui.browser.ExternalLinkOpener
import com.strimup.core.ui.browser.rememberExternalLinkOpener

sealed interface PlayingVideo {
    val title: String

    data class Hosted(val url: String, override val title: String) : PlayingVideo

    data class Embedded(
        val embed: VideoEmbed,
        val originalUrl: String,
        override val title: String,
    ) : PlayingVideo
}

@Stable
class StreamerVideoOpener(private val openExternalLink: ExternalLinkOpener) {
    var playingVideo by mutableStateOf<PlayingVideo?>(null)
        private set

    fun open(video: Streamer.Video) {
        val embed = video.embed
        if (embed != null) {
            playingVideo = PlayingVideo.Embedded(embed = embed, originalUrl = video.url, title = video.title)
            return
        }

        when (val source = video.source()) {
            is VideoSource.HostedFile -> playingVideo = PlayingVideo.Hosted(url = source.url, title = video.title)
            is VideoSource.ExternalLink -> openExternalLink(source.url)
            VideoSource.Unavailable -> Unit
        }
    }

    fun openOriginal(video: PlayingVideo.Embedded) {
        openExternalLink(video.originalUrl)
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
    when (val video = opener.playingVideo) {
        is PlayingVideo.Hosted -> HostedVideoPlayerDialog(
            url = video.url,
            title = video.title,
            onDismiss = opener::close,
        )

        is PlayingVideo.Embedded -> EmbedVideoPlayerDialog(
            embed = video.embed,
            title = video.title,
            onWatchOnPlatformClick = { opener.openOriginal(video) },
            onDismiss = opener::close,
        )

        null -> Unit
    }
}
