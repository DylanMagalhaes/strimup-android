package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.entity.CutLike

fun interface LikeCutUseCase {
    suspend operator fun invoke(videoId: String, isLiked: Boolean): Result<CutLike>
}
