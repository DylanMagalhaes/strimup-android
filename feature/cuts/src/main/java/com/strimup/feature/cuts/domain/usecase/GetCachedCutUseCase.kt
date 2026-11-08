package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.entity.Cut

fun interface GetCachedCutUseCase {
    operator fun invoke(videoId: String): Cut?
}
