package com.strimup.feature.profile.presentation.mapper

import com.strimup.R
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity.Social.Type.Instagram
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity.Social.Type.Kick
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity.Social.Type.Tiktok
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity.Social.Type.Twitch
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity.Social.Type.Youtube
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity


fun ProfileStreamerEntity.Social.getIconRes(): Int =
    when (this.type) {
        Twitch -> R.drawable.ic_twitch
        Youtube -> R.drawable.ic_youtube
        Kick -> R.drawable.ic_kick
        Tiktok -> R.drawable.ic_tiktok
        Instagram -> R.drawable.ic_instagram
    }
