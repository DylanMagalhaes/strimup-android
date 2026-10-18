package com.strimup.feature.streamervideos.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.streamervideos.domain.FakeStreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoFormat
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultUploadVideoUseCaseTest {

    private val file = LocalVideoFile(
        uri = "content://video/1",
        fileName = "clip.mov",
        mimeType = "video/quicktime",
        sizeBytes = 10_000L,
    )

    private suspend fun rejectionOf(block: suspend () -> Unit): InvalidVideoException.Reason? =
        (runCatching { block() }.exceptionOrNull() as? InvalidVideoException)?.reason

    @Test
    fun `a valid video should be uploaded with a trimmed title and the detected format`() = runTest {
        val repository = FakeStreamerVideoRepository(uploadEvents = flowOf(VideoUploadEvent.Progress(100)))
        val useCase = DefaultUploadVideoUseCase(repository)

        val events = useCase(file, title = "  Mon clip  ", description = "  Best of  ").toList()

        assertThat(events).containsExactly(VideoUploadEvent.Progress(100))
        val sent = repository.uploadedVideos.single()
        assertThat(sent.title).isEqualTo("Mon clip")
        assertThat(sent.description).isEqualTo("Best of")
        assertThat(sent.format).isEqualTo(VideoFormat.MOV)
    }

    @Test
    fun `a blank description should not be sent`() = runTest {
        val repository = FakeStreamerVideoRepository()
        val useCase = DefaultUploadVideoUseCase(repository)

        useCase(file, title = "Mon clip", description = "   ").toList()

        assertThat(repository.uploadedVideos.single().description).isNull()
    }

    @Test
    fun `an invalid title should be rejected without calling the server`() = runTest {
        val repository = FakeStreamerVideoRepository()
        val useCase = DefaultUploadVideoUseCase(repository)

        val reason = rejectionOf { useCase(file, title = "a", description = "").toList() }

        assertThat(reason).isEqualTo(InvalidVideoException.Reason.INVALID_TITLE)
        assertThat(repository.uploadedVideos).isEmpty()
    }

    @Test
    fun `a description over 1000 characters should be rejected`() = runTest {
        val useCase = DefaultUploadVideoUseCase(FakeStreamerVideoRepository())

        val reason = rejectionOf { useCase(file, title = "Mon clip", description = "a".repeat(1001)).toList() }

        assertThat(reason).isEqualTo(InvalidVideoException.Reason.INVALID_DESCRIPTION)
    }

    @Test
    fun `an unsupported or too large file should be rejected`() = runTest {
        val useCase = DefaultUploadVideoUseCase(FakeStreamerVideoRepository())

        val unsupported = rejectionOf {
            useCase(file.copy(fileName = "clip.mkv", mimeType = "video/x-matroska"), "Mon clip", "").toList()
        }
        val tooLarge = rejectionOf {
            useCase(file.copy(sizeBytes = 100L * 1024 * 1024), "Mon clip", "").toList()
        }

        assertThat(unsupported).isEqualTo(InvalidVideoException.Reason.UNSUPPORTED_FORMAT)
        assertThat(tooLarge).isEqualTo(InvalidVideoException.Reason.FILE_TOO_LARGE)
    }
}
