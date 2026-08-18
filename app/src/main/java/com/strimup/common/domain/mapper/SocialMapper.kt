package com.strimup.common.domain.mapper

import com.strimup.R
import com.strimup.common.domain.entity.StreamerEntity
import com.strimup.common.domain.entity.StreamerEntity.Social.Type.Instagram
import com.strimup.common.domain.entity.StreamerEntity.Social.Type.Kick
import com.strimup.common.domain.entity.StreamerEntity.Social.Type.Tiktok
import com.strimup.common.domain.entity.StreamerEntity.Social.Type.Twitch
import com.strimup.common.domain.entity.StreamerEntity.Social.Type.Youtube

fun StreamerEntity.Social.getIconRes(): Int =
    when (this.type) {
        Twitch -> R.drawable.ic_twitch
        Youtube -> R.drawable.ic_youtube
        Kick -> R.drawable.ic_kick
        Tiktok -> R.drawable.ic_tiktok
        Instagram -> R.drawable.ic_instagram
    }

