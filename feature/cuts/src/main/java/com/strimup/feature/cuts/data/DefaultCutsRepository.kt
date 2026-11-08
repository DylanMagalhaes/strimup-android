package com.strimup.feature.cuts.data

import com.strimup.core.common.DomainException
import com.strimup.core.network.apiErrorMessage
import com.strimup.core.network.toDomainError
import com.strimup.feature.cuts.data.mapper.toEntity
import com.strimup.feature.cuts.domain.CutsRepository
import com.strimup.feature.cuts.domain.entity.CutLike
import com.strimup.feature.cuts.domain.entity.CutUnavailableException
import com.strimup.feature.cuts.domain.entity.CutsPage
import com.strimup.feature.cuts.domain.entity.ExpiredCutsCursorException
import com.strimup.feature.cuts.domain.entity.LikeRateLimitedException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import javax.inject.Inject

private const val HTTP_BAD_REQUEST = 400
private const val HTTP_NOT_FOUND = 404
private const val HTTP_TOO_MANY_REQUESTS = 429

class DefaultCutsRepository @Inject constructor(
    private val service: CutsApiService,
) : CutsRepository {

    override suspend fun getCuts(cursor: String?, limit: Int): Result<CutsPage> =
        cutsCall(
            mapHttpError = { exception ->
                ExpiredCutsCursorException().takeIf { cursor != null && exception.code() == HTTP_BAD_REQUEST }
            },
        ) {
            service.getCuts(limit = limit, cursor = cursor).toEntity()
        }

    override suspend fun like(videoId: String): Result<CutLike> =
        cutsCall(mapHttpError = ::likeError) { service.like(videoId).toEntity() }

    override suspend fun unlike(videoId: String): Result<CutLike> =
        cutsCall(mapHttpError = ::likeError) { service.unlike(videoId).toEntity() }

    private fun likeError(exception: HttpException): Throwable? = when (exception.code()) {
        HTTP_NOT_FOUND -> CutUnavailableException()
        HTTP_TOO_MANY_REQUESTS -> LikeRateLimitedException(exception.apiErrorMessage())
        else -> null
    }

    private suspend fun <T> cutsCall(
        mapHttpError: (HttpException) -> Throwable?,
        block: suspend () -> T,
    ): Result<T> {
        val result = runCatching { block() }
        val error = result.exceptionOrNull() ?: return result
        if (error is CancellationException) throw error
        val mappedError = (error as? HttpException)?.let(mapHttpError)
        return Result.failure(mappedError ?: DomainException(error.toDomainError()))
    }
}
