package com.strimup.feature.streamerprofile.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.core.user.domain.UserRepository
import com.strimup.core.user.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultUpdateAvatarUseCaseTest {

    private class FakeUserRepository : UserRepository {
        val updatedAvatarUrls = mutableListOf<String>()

        override fun getCurrentUser(): Flow<UserEntity?> = flowOf(null)

        override suspend fun updateCurrentUserAvatar(avatarUrl: String) {
            updatedAvatarUrls += avatarUrl
        }
    }

    private class FakeStreamerRepository(private val avatarResult: Result<String>) : StreamerRepository {
        override suspend fun updateAvatar(uri: String): Result<String> = avatarResult

        override suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> =
            error("unused")

        override suspend fun getLiveStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> =
            error("unused")

        override suspend fun searchStreamers(userName: String): Result<List<Streamer>> = error("unused")
        override suspend fun getStreamerById(id: String): Result<Streamer> = error("unused")
        override suspend fun updateProfile(streamer: Streamer): Result<Streamer> = error("unused")
        override suspend fun getStreamerOptions(): Result<StreamerOptions> = error("unused")

        override suspend fun getStreamersByFilter(request: StreamerMatchRequest): Result<StreamerMatchResult> =
            error("unused")
    }

    @Test
    fun `successful upload should update the local user avatar`() = runTest {
        val userRepository = FakeUserRepository()
        val useCase = DefaultUpdateAvatarUseCase(
            FakeStreamerRepository(Result.success("https://cdn/avatar.jpg")),
            userRepository,
        )

        val result = useCase("content://media/42")

        assertThat(result.getOrNull()).isEqualTo("https://cdn/avatar.jpg")
        assertThat(userRepository.updatedAvatarUrls).containsExactly("https://cdn/avatar.jpg")
    }

    @Test
    fun `failed upload should keep the local user avatar`() = runTest {
        val userRepository = FakeUserRepository()
        val useCase = DefaultUpdateAvatarUseCase(
            FakeStreamerRepository(Result.failure(IllegalStateException())),
            userRepository,
        )

        val result = useCase("content://media/42")

        assertThat(result.isFailure).isTrue()
        assertThat(userRepository.updatedAvatarUrls).isEmpty()
    }
}
