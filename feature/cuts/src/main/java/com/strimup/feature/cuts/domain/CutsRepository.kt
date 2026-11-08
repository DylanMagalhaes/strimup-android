package com.strimup.feature.cuts.domain

import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.CutLike
import com.strimup.feature.cuts.domain.entity.CutsPage

interface CutsRepository {
    suspend fun getCuts(cursor: String?, limit: Int): Result<CutsPage>

    suspend fun like(videoId: String): Result<CutLike>

    suspend fun unlike(videoId: String): Result<CutLike>

    fun cachedCut(videoId: String): Cut?
}
