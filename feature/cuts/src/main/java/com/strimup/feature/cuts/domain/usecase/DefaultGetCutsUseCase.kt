package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.CutsRepository
import com.strimup.feature.cuts.domain.entity.CutsPage
import javax.inject.Inject

const val CUTS_PAGE_SIZE = 10

class DefaultGetCutsUseCase @Inject constructor(
    private val repository: CutsRepository,
) : GetCutsUseCase {
    override suspend fun invoke(cursor: String?): Result<CutsPage> =
        repository.getCuts(cursor = cursor, limit = CUTS_PAGE_SIZE)
}
