package com.strimup.feature.home.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.feature.home.domain.entity.FilterEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetStreamersWithoutFavoriteUseCaseTest {

    private val discoveryStreamers = listOf(
        Streamer(id = "1", userName = "inox", imageUrl = ""),
        Streamer(id = "2", userName = "gotaga", imageUrl = ""),
    )
    private val liveStreamers = listOf(
        Streamer(id = "3", userName = "squeezie", imageUrl = ""),
    )

    @Test
    fun `invoke with Discovery filter should return random streamers`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository(
            randomStreamers = Result.success(discoveryStreamers),
        )
        val useCase = GetStreamersWithoutFavoriteUseCase(repository)

        // WHEN
        val result = useCase(FilterEntity.Discovery)

        // THEN
        assertThat(result.getOrNull()).isEqualTo(discoveryStreamers)
        assertThat(repository.randomStreamersCallArgs).containsExactly(emptyList<String>())
        assertThat(repository.liveStreamersCallArgs).isEmpty()
    }

    @Test
    fun `invoke with Live filter should return live streamers`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository(
            liveStreamers = Result.success(liveStreamers),
        )
        val useCase = GetStreamersWithoutFavoriteUseCase(repository)

        // WHEN
        val result = useCase(FilterEntity.Live)

        // THEN
        assertThat(result.getOrNull()).isEqualTo(liveStreamers)
        assertThat(repository.liveStreamersCallArgs).containsExactly(emptyList<String>())
        assertThat(repository.randomStreamersCallArgs).isEmpty()
    }

    @Test
    fun `invoke with Discovery filter should propagate repository failure`() = runTest {
        // GIVEN
        val error = IllegalStateException("network error")
        val repository = FakeStreamerRepository(
            randomStreamers = Result.failure(error),
        )
        val useCase = GetStreamersWithoutFavoriteUseCase(repository)

        // WHEN
        val result = useCase(FilterEntity.Discovery)

        // THEN
        assertThat(result.exceptionOrNull()).isEqualTo(error)
    }

    @Test
    fun `invoke with Live filter should propagate repository failure`() = runTest {
        // GIVEN
        val error = IllegalStateException("network error")
        val repository = FakeStreamerRepository(
            liveStreamers = Result.failure(error),
        )
        val useCase = GetStreamersWithoutFavoriteUseCase(repository)

        // WHEN
        val result = useCase(FilterEntity.Live)

        // THEN
        assertThat(result.exceptionOrNull()).isEqualTo(error)
    }

    private class FakeStreamerRepository(
        private val randomStreamers: Result<List<Streamer>> = Result.success(emptyList()),
        private val liveStreamers: Result<List<Streamer>> = Result.success(emptyList()),
    ) : StreamerRepository {

        val randomStreamersCallArgs = mutableListOf<List<String>>()
        val liveStreamersCallArgs = mutableListOf<List<String>>()

        override suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> {
            randomStreamersCallArgs += favoriteStreamerIds
            return randomStreamers
        }

        override suspend fun getLiveStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> {
            liveStreamersCallArgs += favoriteStreamerIds
            return liveStreamers
        }

        override suspend fun searchStreamers(userName: String): Result<List<Streamer>> = TODO()

        override suspend fun getStreamerById(id: String): Result<Streamer> = TODO()

        override suspend fun updateProfile(streamer: Streamer): Result<Streamer> = TODO()

        override suspend fun updateAvatar(uri: String): Result<String> = TODO()

        override suspend fun getStreamerOptions(): Result<StreamerOptions> = TODO()

        override suspend fun getStreamersByFilter(request: StreamerMatchRequest): Result<StreamerMatchResult> = TODO()
    }
}
