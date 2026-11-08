package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.CutsRepository
import com.strimup.feature.cuts.domain.entity.Cut
import javax.inject.Inject

class DefaultGetCachedCutUseCase @Inject constructor(
    private val repository: CutsRepository,
) : GetCachedCutUseCase {
    override fun invoke(videoId: String): Cut? = repository.cachedCut(videoId)
}
