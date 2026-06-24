package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity

fun interface GetStreamerUsecase {
    suspend operator fun invoke(id: String): Result<StreamerDetailEntity>
}