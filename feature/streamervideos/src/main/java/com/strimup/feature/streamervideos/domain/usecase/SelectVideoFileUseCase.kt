package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile

fun interface SelectVideoFileUseCase {
    suspend operator fun invoke(uri: String): Result<LocalVideoFile>
}
