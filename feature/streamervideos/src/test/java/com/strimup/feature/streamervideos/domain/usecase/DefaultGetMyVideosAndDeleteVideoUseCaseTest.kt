package com.strimup.feature.streamervideos.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.FakeStreamerVideoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultGetMyVideosAndDeleteVideoUseCaseTest {

    @Test
    fun `get my videos should return the repository videos`() = runTest {
        val videos = listOf(Streamer.Video(id = "v1", title = "Clip", description = "", url = "u", order = 1))
        val useCase = DefaultGetMyVideosUseCase(FakeStreamerVideoRepository(myVideosResult = Result.success(videos)))

        assertThat(useCase().getOrNull()).isEqualTo(videos)
    }

    @Test
    fun `delete video should delete the given id`() = runTest {
        val repository = FakeStreamerVideoRepository()

        val result = DefaultDeleteVideoUseCase(repository)("v1")

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.deletedIds).containsExactly("v1")
    }
}
