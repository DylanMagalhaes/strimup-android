package com.strimup.core.ui.streamer

import com.strimup.core.streamer.domain.entity.Social

fun Social.Type.displayName(): String = when (this) {
    Social.Type.Twitch -> "Twitch"
    Social.Type.Kick -> "Kick"
    Social.Type.Youtube -> "YouTube"
    Social.Type.Instagram -> "Instagram"
    Social.Type.Tiktok -> "TikTok"
}
