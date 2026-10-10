package com.strimup.feature.streamerdetail.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.streamer.domain.repository.StreamerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGetStreamerUseCaseTest {

    private val fakeStreamer = Streamer(id = "1", userName = "inox", imageUrl = "")

    private fun buildUseCase(
        streamerRepository: FakeStreamerRepository = FakeStreamerRepository(),
        favoriteRepository: FakeFavoriteStreamerRepository = FakeFavoriteStreamerRepository(),
    ) = DefaultGetStreamerUseCase(streamerRepository, favoriteRepository)

    @Test
    fun `invoke when the streamer is in the favorites list should return isFavorite true`() = runTest {
        // GIVEN
        val useCase = buildUseCase(
            streamerRepository = FakeStreamerRepository(result = Result.success(fakeStreamer)),
            favoriteRepository = FakeFavoriteStreamerRepository(favorites = listOf(fakeStreamer)),
        )

        // WHEN
        val result = useCase("1")

        // THEN
        val detail = result.getOrNull()
        assertThat(detail?.streamer).isEqualTo(fakeStreamer)
        assertThat(detail?.isFavorite).isTrue()
    }

    @Test
    fun `invoke when the streamer is not in the favorites list should return isFavorite false`() = runTest {
        // GIVEN
        val useCase = buildUseCase(
            favoriteRepository = FakeFavoriteStreamerRepository(favorites = emptyList()),
        )

        // WHEN
        val result = useCase("1")

        // THEN
        assertThat(result.getOrNull()?.isFavorite).isFalse()
    }

    @Test
    fun `invoke when the favorites list contains other streamers should return isFavorite false`() = runTest {
        // GIVEN
        val otherFavorite = Streamer(id = "2", userName = "gotaga", imageUrl = "")
        val useCase = buildUseCase(
            favoriteRepository = FakeFavoriteStreamerRepository(favorites = listOf(otherFavorite)),
        )

        // WHEN
        val result = useCase("1")

        // THEN
        assertThat(result.getOrNull()?.isFavorite).isFalse()
    }

    @Test
    fun `invoke when getStreamerById fails should propagate the failure without checking favorites`() = runTest {
        // GIVEN
        val error = Exception("Streamer introuvable")
        val streamerRepository = FakeStreamerRepository(result = Result.failure(error))
        val favoriteRepository = FakeFavoriteStreamerRepository(favorites = emptyList())
        val useCase = buildUseCase(streamerRepository, favoriteRepository)

        // WHEN
        val result = useCase("1")

        // THEN
        assertThat(result.exceptionOrNull()).isEqualTo(error)
        assertThat(favoriteRepository.observeFavoritesCallCount).isEqualTo(0)
    }

    private class FakeStreamerRepository(
        private val result: Result<Streamer> = Result.success(Streamer(id = "1", userName = "inox", imageUrl = "")),
    ) : StreamerRepository {
        var getStreamerByIdCallCount = 0

        override suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> = TODO()

        override suspend fun getLiveStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> = TODO()

        override suspend fun searchStreamers(userName: String): Result<List<Streamer>> = TODO()

        override suspend fun getStreamerById(id: String): Result<Streamer> {
            getStreamerByIdCallCount++
            return result
        }

        override suspend fun updateProfile(streamer: Streamer): Result<Streamer> = TODO()

        override suspend fun updateAvatar(uri: String): Result<String> = TODO()

        override suspend fun getStreamerOptions(): Result<StreamerOptions> = TODO()

        override suspend fun getStreamersByFilter(request: StreamerMatchRequest): Result<StreamerMatchResult> = TODO()
    }

    private class FakeFavoriteStreamerRepository(
        private val favorites: List<Streamer> = emptyList(),
    ) : FavoriteStreamerRepository {
        var observeFavoritesCallCount = 0

        override fun observeFavorites(): Flow<List<Streamer>> {
            observeFavoritesCallCount++
            return flowOf(favorites)
        }

        override suspend fun refreshFavoriteStreamers(): Result<Unit> = TODO()

        override suspend fun addFavoriteStreamer(id: String): Result<Unit> = TODO()

        override suspend fun deleteFavoriteStreamer(id: String): Result<Unit> = TODO()
    }
}
