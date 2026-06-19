package com.strimup.feature.profile.domain.usecase

import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity

fun interface GetStreamerUsecase {
    suspend operator fun invoke(id: String): ProfileStreamerEntity
}