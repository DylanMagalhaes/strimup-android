package com.strimup.core.streamer.domain.mapper

import com.strimup.R
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Social.Type.Instagram
import com.strimup.core.streamer.domain.entity.Social.Type.Kick
import com.strimup.core.streamer.domain.entity.Social.Type.Tiktok
import com.strimup.core.streamer.domain.entity.Social.Type.Twitch
import com.strimup.core.streamer.domain.entity.Social.Type.Youtube

fun Social.getIconRes(): Int =
    when (this.type) {
        Twitch -> R.drawable.ic_twitch
        Youtube -> R.drawable.ic_youtube
        Kick -> R.drawable.ic_kick
        Tiktok -> R.drawable.ic_tiktok
        Instagram -> R.drawable.ic_instagram
    }
