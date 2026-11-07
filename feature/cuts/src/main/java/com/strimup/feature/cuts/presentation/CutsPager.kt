package com.strimup.feature.cuts.presentation

import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.ExpiredCutsCursorException
import com.strimup.feature.cuts.domain.usecase.GetCutsUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private val RETRY_DELAYS = listOf(1.seconds, 2.seconds, 4.seconds)
private const val FIRST_SERVER_ERROR_CODE = 500

class CutsPager @Inject constructor(
    private val getCuts: GetCutsUseCase,
) {
    private var cursor: String? = null

    suspend fun loadNextPage(): Result<List<Cut>> {
        var attempt = 0
        while (true) {
            val result = getCuts(cursor)
            val page = result.getOrNull()
            if (page != null) {
                cursor = page.nextCursor
                return Result.success(page.cuts)
            }

            val error = checkNotNull(result.exceptionOrNull())
            when {
                error is ExpiredCutsCursorException -> cursor = null
                error.isRetryable() && attempt < RETRY_DELAYS.size -> delay(RETRY_DELAYS[attempt++])
                else -> return Result.failure(error)
            }
        }
    }

    private fun Throwable.isRetryable(): Boolean = when (val domainError = (this as? DomainException)?.error) {
        is DomainError.Server -> domainError.code >= FIRST_SERVER_ERROR_CODE
        DomainError.Network, DomainError.Timeout -> true
        else -> false
    }
}
