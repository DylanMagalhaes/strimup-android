package com.strimup.feature.streamerprofile.presentation.mapper

import com.strimup.R
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity


fun StreamerProfileEntity.Social.getIconRes(): Int =
    when (this.type) {
        StreamerProfileEntity.Social.Type.Twitch -> R.drawable.ic_twitch
        StreamerProfileEntity.Social.Type.Youtube -> R.drawable.ic_youtube
        StreamerProfileEntity.Social.Type.Kick -> R.drawable.ic_kick
        StreamerProfileEntity.Social.Type.Tiktok -> R.drawable.ic_tiktok
        StreamerProfileEntity.Social.Type.Instagram -> R.drawable.ic_instagram
    }


