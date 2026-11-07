package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.entity.CutsPage

fun interface GetCutsUseCase {
    suspend operator fun invoke(cursor: String?): Result<CutsPage>
}
