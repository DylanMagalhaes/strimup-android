package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.CutsRepository
import com.strimup.feature.cuts.domain.entity.CutLike
import javax.inject.Inject

class DefaultLikeCutUseCase @Inject constructor(
    private val repository: CutsRepository,
) : LikeCutUseCase {
    override suspend fun invoke(videoId: String, isLiked: Boolean): Result<CutLike> =
        if (isLiked) repository.like(videoId) else repository.unlike(videoId)
}
