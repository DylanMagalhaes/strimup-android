package com.strimup.feature.streamervideos.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.FakeStreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideoLink
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultAddVideoLinkUseCaseTest {

    private val addedVideo = Streamer.Video(
        id = "v1",
        title = "Mon clip",
        description = "",
        url = "https://www.youtube.com/watch?v=abc",
        order = 1,
    )

    private fun reasonOf(result: Result<Streamer.Video>) =
        (result.exceptionOrNull() as InvalidVideoException).reason

    @Test
    fun `a valid link should be sent trimmed and normalized`() = runTest {
        val repository = FakeStreamerVideoRepository(addLinkResult = Result.success(addedVideo))

        val result = DefaultAddVideoLinkUseCase(repository)(" youtu.be/abc ", "  Mon clip ", "  ")

        assertThat(result.getOrThrow()).isEqualTo(addedVideo)
        assertThat(repository.addedLinks).containsExactly(
            NewStreamerVideoLink(url = "https://youtu.be/abc", title = "Mon clip", description = null),
        )
    }

    @Test
    fun `an unsupported link should be rejected without calling the server`() = runTest {
        val repository = FakeStreamerVideoRepository()

        val result = DefaultAddVideoLinkUseCase(repository)("https://vimeo.com/1", "Mon clip", "")

        assertThat(reasonOf(result)).isEqualTo(InvalidVideoException.Reason.INVALID_LINK)
        assertThat(repository.addedLinks).isEmpty()
    }

    @Test
    fun `a too short title should be rejected`() = runTest {
        val result = DefaultAddVideoLinkUseCase(FakeStreamerVideoRepository())("https://youtu.be/abc", "a", "")

        assertThat(reasonOf(result)).isEqualTo(InvalidVideoException.Reason.INVALID_TITLE)
    }

    @Test
    fun `a too long description should be rejected`() = runTest {
        val result = DefaultAddVideoLinkUseCase(FakeStreamerVideoRepository())(
            "https://youtu.be/abc",
            "Mon clip",
            "a".repeat(1001),
        )

        assertThat(reasonOf(result)).isEqualTo(InvalidVideoException.Reason.INVALID_DESCRIPTION)
    }
}
