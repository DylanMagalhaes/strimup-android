package com.strimup.core.ui.component.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoOrientation
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source

@Immutable
data class StreamerVideoPreviewModel(
    val video: Streamer.Video,
    val source: VideoSource,
    val orientation: VideoOrientation,
    val hostedFrame: ImageBitmap?,
) {
    val isPlayable: Boolean
        get() = video.embed != null || source !is VideoSource.Unavailable
}

@Composable
fun rememberStreamerVideoPreview(video: Streamer.Video): StreamerVideoPreviewModel {
    val source = remember(video.url) { video.source() }
    val hostedUrl = (source as? VideoSource.HostedFile)?.url?.takeIf { video.embed == null }
    val hostedFrame by produceState<HostedVideoFrame?>(initialValue = null, hostedUrl) {
        value = hostedUrl?.let { VideoFrameLoader.load(it) }
    }
    val hostedBitmap = remember(hostedFrame) { hostedFrame?.bitmap?.asImageBitmap() }

    return StreamerVideoPreviewModel(
        video = video,
        source = source,
        orientation = video.embed?.orientation ?: hostedFrame?.orientation ?: VideoOrientation.HORIZONTAL,
        hostedFrame = hostedBitmap,
    )
}

fun StreamerVideoPreviewModel.width(horizontalWidth: Dp, verticalWidth: Dp): Dp =
    if (orientation == VideoOrientation.VERTICAL) verticalWidth else horizontalWidth
