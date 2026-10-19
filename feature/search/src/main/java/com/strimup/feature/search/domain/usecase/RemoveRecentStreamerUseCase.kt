package com.strimup.feature.search.domain.usecase

fun interface RemoveRecentStreamerUseCase {
    suspend operator fun invoke(id: String)
}
