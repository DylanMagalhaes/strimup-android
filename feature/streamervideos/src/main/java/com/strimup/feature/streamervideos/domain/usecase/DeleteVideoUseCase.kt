package com.strimup.feature.streamervideos.domain.usecase

fun interface DeleteVideoUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}
