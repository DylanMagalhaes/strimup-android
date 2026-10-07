package com.strimup.feature.streamervideos.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.streamervideos.domain.FakeStreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultSelectVideoFileUseCaseTest {

    private val validFile = LocalVideoFile(
        uri = "content://video/1",
        fileName = "clip.mp4",
        mimeType = "video/mp4",
        sizeBytes = 10_000L,
    )

    private fun useCaseReturning(result: Result<LocalVideoFile>) =
        DefaultSelectVideoFileUseCase(FakeStreamerVideoRepository(localFileResult = result))

    @Test
    fun `a supported file under 80 MB should be selected`() = runTest {
        val useCase = useCaseReturning(Result.success(validFile))

        assertThat(useCase("content://video/1").getOrNull()).isEqualTo(validFile)
    }

    @Test
    fun `an unsupported format should be rejected before any upload`() = runTest {
        val avi = validFile.copy(fileName = "clip.avi", mimeType = "video/x-msvideo")
        val useCase = useCaseReturning(Result.success(avi))

        val error = useCase("content://video/1").exceptionOrNull()

        assertThat((error as InvalidVideoException).reason).isEqualTo(InvalidVideoException.Reason.UNSUPPORTED_FORMAT)
    }

    @Test
    fun `a file over 80 MB should be rejected before any upload`() = runTest {
        val big = validFile.copy(sizeBytes = 81L * 1024 * 1024)
        val useCase = useCaseReturning(Result.success(big))

        val error = useCase("content://video/1").exceptionOrNull()

        assertThat((error as InvalidVideoException).reason).isEqualTo(InvalidVideoException.Reason.FILE_TOO_LARGE)
    }

    @Test
    fun `an unreadable file should keep the repository error`() = runTest {
        val failure = IllegalStateException()
        val useCase = useCaseReturning(Result.failure(failure))

        assertThat(useCase("content://video/1").exceptionOrNull()).isSameInstanceAs(failure)
    }
}
