package com.strimup.feature.home.presentation.mapper

import com.strimup.R
import com.strimup.feature.home.domain.entity.StreamerEntity
import com.strimup.feature.home.domain.entity.StreamerEntity.Social.Type.Instagram
import com.strimup.feature.home.domain.entity.StreamerEntity.Social.Type.Kick
import com.strimup.feature.home.domain.entity.StreamerEntity.Social.Type.Tiktok
import com.strimup.feature.home.domain.entity.StreamerEntity.Social.Type.Twitch
import com.strimup.feature.home.domain.entity.StreamerEntity.Social.Type.Youtube

fun StreamerEntity.Social.getIconRes(): Int =
    when (this.type) {
        Twitch -> R.drawable.ic_twitch
        Youtube -> R.drawable.ic_youtube
        Kick -> R.drawable.ic_kick
        Tiktok -> R.drawable.ic_tiktok
        Instagram -> R.drawable.ic_instagram
    }

