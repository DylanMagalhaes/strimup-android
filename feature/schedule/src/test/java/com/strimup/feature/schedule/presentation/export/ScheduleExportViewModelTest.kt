package com.strimup.feature.schedule.presentation.export

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.feature.schedule.R
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.usecase.GetMyScheduleUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.DayOfWeek
import com.strimup.core.ui.R as CoreUiR

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleExportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private data class GenerateCall(
        val username: String,
        val days: List<ScheduleExportDay>,
        val template: ScheduleExportTemplate,
    )

    private val mySchedule = listOf(
        ScheduleItemEntity(id = "1", title = "GTA RP", dayOfWeek = 0, startTime = "20:00"),
    )

    private val generateCalls = mutableListOf<GenerateCall>()

    private class FakeGallerySaver(
        override val isAvailable: Boolean = true,
        private val result: () -> Result<Unit> = { Result.success(Unit) },
    ) : ScheduleImageGallerySaver {
        val savedFiles = mutableListOf<File>()

        override suspend fun save(file: File): Result<Unit> {
            savedFiles += file
            return result()
        }
    }

    private fun createViewModel(
        getMySchedule: GetMyScheduleUseCase = GetMyScheduleUseCase { Result.success(mySchedule) },
        generate: suspend (ScheduleExportTemplate) -> Result<File> = { template ->
            Result.success(File("planning-${template.name}.png"))
        },
        gallerySaver: ScheduleImageGallerySaver = FakeGallerySaver(),
    ) = ScheduleExportViewModel(
        gallerySaver = gallerySaver,
        getMySchedule = getMySchedule,
        imageGenerator = ScheduleExportImageGenerator { username, days, template ->
            generateCalls += GenerateCall(username, days, template)
            generate(template)
        },
    )

    @Test
    fun `initial state should be preparing the image with the black template`() {
        val viewModel = createViewModel()

        assertThat(viewModel.state.value).isEqualTo(ScheduleExportUiState(isGallerySaveAvailable = true))
        assertThat(viewModel.state.value.selectedTemplate).isEqualTo(ScheduleExportTemplate.Black)
    }

    @Test
    fun `load should generate the image of the connected streamer schedule with the black template`() = runTest {
        // GIVEN
        val viewModel = createViewModel()

        // WHEN
        viewModel.load("raziuko")
        advanceUntilIdle()

        // THEN
        val call = generateCalls.single()
        assertThat(call.username).isEqualTo("raziuko")
        assertThat(call.template).isEqualTo(ScheduleExportTemplate.Black)
        assertThat(call.days.first().dayOfWeek).isEqualTo(DayOfWeek.MONDAY)
        assertThat(call.days.first().slots.single().title).isEqualTo("GTA RP")
        assertThat(viewModel.state.value.imageFile).isEqualTo(File("planning-Black.png"))
        assertThat(viewModel.state.value.isImageReady).isTrue()
    }

    @Test
    fun `isImageReady should be false while the image is being prepared`() = runTest {
        // GIVEN
        val pendingImage = CompletableDeferred<Result<File>>()
        val viewModel = createViewModel(generate = { pendingImage.await() })

        // WHEN
        viewModel.load("raziuko")
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.isGenerating).isTrue()
        assertThat(viewModel.state.value.isImageReady).isFalse()

        pendingImage.complete(Result.success(File("planning.png")))
        advanceUntilIdle()
        assertThat(viewModel.state.value.isImageReady).isTrue()
    }

    @Test
    fun `onTemplateSelected should regenerate the image with the new template`() = runTest {
        // GIVEN
        val viewModel = createViewModel()
        viewModel.load("raziuko")
        advanceUntilIdle()

        // WHEN
        viewModel.onTemplateSelected(ScheduleExportTemplate.Pink)
        advanceUntilIdle()

        // THEN
        assertThat(generateCalls.map { it.template })
            .containsExactly(ScheduleExportTemplate.Black, ScheduleExportTemplate.Pink)
            .inOrder()
        assertThat(viewModel.state.value.selectedTemplate).isEqualTo(ScheduleExportTemplate.Pink)
        assertThat(viewModel.state.value.imageFile).isEqualTo(File("planning-Pink.png"))
    }

    @Test
    fun `selecting the current template again should not regenerate the image`() = runTest {
        // GIVEN
        val viewModel = createViewModel()
        viewModel.load("raziuko")
        advanceUntilIdle()

        // WHEN
        viewModel.onTemplateSelected(ScheduleExportTemplate.Black)
        advanceUntilIdle()

        // THEN
        assertThat(generateCalls).hasSize(1)
    }

    @Test
    fun `a generation failure should show the generation error and retry should generate again`() = runTest {
        // GIVEN
        var shouldFail = true
        val viewModel = createViewModel(
            generate = { template ->
                if (shouldFail) Result.failure(IllegalStateException()) else Result.success(File("$template.png"))
            },
        )
        viewModel.load("raziuko")
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(R.string.schedule_export_error))
        assertThat(viewModel.state.value.isGenerating).isFalse()

        // WHEN
        shouldFail = false
        viewModel.onRetryClick()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.errorMessage).isNull()
        assertThat(viewModel.state.value.isImageReady).isTrue()
    }

    @Test
    fun `a schedule loading failure should show the mapped error and retry should load again`() = runTest {
        // GIVEN
        var loadCalls = 0
        val viewModel = createViewModel(
            getMySchedule = GetMyScheduleUseCase {
                loadCalls++
                if (loadCalls == 1) Result.failure(DomainException(DomainError.Network)) else Result.success(mySchedule)
            },
        )
        viewModel.load("raziuko")
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_network))
        assertThat(generateCalls).isEmpty()

        // WHEN
        viewModel.onRetryClick()
        advanceUntilIdle()

        // THEN
        assertThat(loadCalls).isEqualTo(2)
        assertThat(viewModel.state.value.isImageReady).isTrue()
    }

    @Test
    fun `load with the same username should not reload once the schedule is loaded`() = runTest {
        // GIVEN
        var loadCalls = 0
        val viewModel = createViewModel(
            getMySchedule = GetMyScheduleUseCase {
                loadCalls++
                Result.success(mySchedule)
            },
        )
        viewModel.load("raziuko")
        advanceUntilIdle()

        // WHEN
        viewModel.load("raziuko")
        advanceUntilIdle()

        // THEN
        assertThat(loadCalls).isEqualTo(1)
    }

    @Test
    fun `gallery save should be hidden when the device does not support it`() {
        val viewModel = createViewModel(gallerySaver = FakeGallerySaver(isAvailable = false))

        assertThat(viewModel.state.value.isGallerySaveAvailable).isFalse()
    }

    @Test
    fun `onSaveToGalleryClick should save the generated image and confirm it`() = runTest {
        // GIVEN
        val gallerySaver = FakeGallerySaver()
        val viewModel = createViewModel(gallerySaver = gallerySaver)
        viewModel.load("raziuko")
        advanceUntilIdle()

        viewModel.events.test {
            // WHEN
            viewModel.onSaveToGalleryClick()
            advanceUntilIdle()

            // THEN
            assertThat(awaitItem()).isEqualTo(ScheduleExportUiEvent.ShowSnackBar(R.string.schedule_export_saved))
        }
        assertThat(gallerySaver.savedFiles).containsExactly(File("planning-Black.png"))
        assertThat(viewModel.state.value.isSavingToGallery).isFalse()
    }

    @Test
    fun `onSaveToGalleryClick when saving fails should show the save error`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            gallerySaver = FakeGallerySaver(result = { Result.failure(IllegalStateException()) }),
        )
        viewModel.load("raziuko")
        advanceUntilIdle()

        viewModel.events.test {
            // WHEN
            viewModel.onSaveToGalleryClick()
            advanceUntilIdle()

            // THEN
            assertThat(awaitItem()).isEqualTo(ScheduleExportUiEvent.ShowSnackBar(R.string.schedule_export_save_error))
        }
    }

    @Test
    fun `onSaveToGalleryClick before the image is ready should do nothing`() = runTest {
        // GIVEN
        val gallerySaver = FakeGallerySaver()
        val viewModel = createViewModel(gallerySaver = gallerySaver)

        // WHEN
        viewModel.onSaveToGalleryClick()
        advanceUntilIdle()

        // THEN
        assertThat(gallerySaver.savedFiles).isEmpty()
    }
}
