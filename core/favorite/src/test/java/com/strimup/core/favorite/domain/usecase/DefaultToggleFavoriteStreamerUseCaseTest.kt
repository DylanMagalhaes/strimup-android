package com.strimup.core.favorite.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.domain.entity.Streamer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultToggleFavoriteStreamerUseCaseTest {

    private val repository = FakeFavoriteStreamerRepository()
    private val useCase = DefaultToggleFavoriteStreamerUseCase(repository)

    @Test
    fun `a streamer that is not a favorite should be added`() = runTest {
        useCase(streamerId = "1", isFavorite = false)

        assertThat(repository.addedIds).containsExactly("1")
        assertThat(repository.deletedIds).isEmpty()
    }

    @Test
    fun `a favorite streamer should be removed`() = runTest {
        useCase(streamerId = "1", isFavorite = true)

        assertThat(repository.deletedIds).containsExactly("1")
        assertThat(repository.addedIds).isEmpty()
    }

    @Test
    fun `a repository failure should be returned`() = runTest {
        val error = IllegalStateException()
        repository.result = Result.failure(error)

        val result = useCase(streamerId = "1", isFavorite = false)

        assertThat(result.exceptionOrNull()).isEqualTo(error)
    }

    private class FakeFavoriteStreamerRepository : FavoriteStreamerRepository {
        val addedIds = mutableListOf<String>()
        val deletedIds = mutableListOf<String>()
        var result: Result<Unit> = Result.success(Unit)

        override fun observeFavorites(): Flow<List<Streamer>> = emptyFlow()

        override suspend fun refreshFavoriteStreamers(): Result<Unit> = result

        override suspend fun addFavoriteStreamer(id: String): Result<Unit> {
            addedIds += id
            return result
        }

        override suspend fun deleteFavoriteStreamer(id: String): Result<Unit> {
            deletedIds += id
            return result
        }
    }
}
