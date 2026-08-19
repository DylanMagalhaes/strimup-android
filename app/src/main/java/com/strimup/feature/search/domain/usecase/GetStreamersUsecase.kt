package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

interface GetStreamersUsecase {

    suspend operator fun invoke(username: String): Result<List<Streamer>>
}
