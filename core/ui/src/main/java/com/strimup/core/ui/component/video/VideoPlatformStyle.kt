package com.strimup.core.ui.component.video

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoOrientation
import com.strimup.core.ui.R
import com.strimup.core.ui.theme.PurpleTwitch
import com.strimup.core.ui.theme.TikTokBlack
import com.strimup.core.ui.theme.YouTubeRed
import com.strimup.core.streamer.R as StreamerR

private const val HORIZONTAL_ASPECT_RATIO = 16f / 9f
private const val VERTICAL_ASPECT_RATIO = 9f / 16f

internal val VideoOrientation.aspectRatio: Float
    get() = when (this) {
        VideoOrientation.VERTICAL -> VERTICAL_ASPECT_RATIO
        VideoOrientation.HORIZONTAL -> HORIZONTAL_ASPECT_RATIO
    }

@get:DrawableRes
internal val VideoEmbed.Provider.iconRes: Int
    get() = when (this) {
        VideoEmbed.Provider.YOUTUBE -> StreamerR.drawable.ic_youtube
        VideoEmbed.Provider.TIKTOK -> StreamerR.drawable.ic_tiktok
        VideoEmbed.Provider.TWITCH -> StreamerR.drawable.ic_twitch
    }

@get:StringRes
internal val VideoEmbed.Provider.labelRes: Int
    get() = when (this) {
        VideoEmbed.Provider.YOUTUBE -> R.string.video_platform_youtube
        VideoEmbed.Provider.TIKTOK -> R.string.video_platform_tiktok
        VideoEmbed.Provider.TWITCH -> R.string.video_platform_twitch
    }

@get:StringRes
internal val VideoEmbed.Provider.watchOnRes: Int
    get() = when (this) {
        VideoEmbed.Provider.YOUTUBE -> R.string.video_watch_on_youtube
        VideoEmbed.Provider.TIKTOK -> R.string.video_watch_on_tiktok
        VideoEmbed.Provider.TWITCH -> R.string.video_watch_on_twitch
    }

internal val VideoEmbed.Provider.brandColor: Color
    get() = when (this) {
        VideoEmbed.Provider.YOUTUBE -> YouTubeRed
        VideoEmbed.Provider.TIKTOK -> TikTokBlack
        VideoEmbed.Provider.TWITCH -> PurpleTwitch
    }

internal val VideoEmbed.Provider.watchButtonColor: Color
    get() = if (this == VideoEmbed.Provider.TIKTOK) Color.White else brandColor

internal val VideoEmbed.Provider.watchButtonContentColor: Color
    get() = if (this == VideoEmbed.Provider.TIKTOK) TikTokBlack else Color.White
