package com.strimup.feature.streamervideos.presentation.videossection

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.feature.streamervideos.R
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import com.strimup.feature.streamervideos.domain.usecase.AddVideoLinkUseCase
import com.strimup.feature.streamervideos.domain.usecase.DeleteVideoUseCase
import com.strimup.feature.streamervideos.domain.usecase.GetMyVideosUseCase
import com.strimup.feature.streamervideos.domain.usecase.SelectVideoFileUseCase
import com.strimup.feature.streamervideos.domain.usecase.UploadVideoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import com.strimup.core.ui.R as CoreUiR

@OptIn(ExperimentalCoroutinesApi::class)
class VideosSectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun video(id: String, order: Int) =
        Streamer.Video(id = id, title = "Clip $id", description = "", url = "https://cdn/$id.mp4", order = order)

    private fun myVideos(vararg videos: Streamer.Video) = GetMyVideosUseCase { Result.success(videos.toList()) }

    private val pickedFile = LocalVideoFile(
        uri = "content://video/1",
        fileName = "clip.mp4",
        mimeType = "video/mp4",
        sizeBytes = 1_000L,
    )

    private fun createViewModel(
        getMyVideos: GetMyVideosUseCase = myVideos(),
        selectVideoFile: SelectVideoFileUseCase = SelectVideoFileUseCase { Result.success(pickedFile) },
        uploadVideo: UploadVideoUseCase = UploadVideoUseCase { _, _, _ -> flowOf() },
        addVideoLink: AddVideoLinkUseCase = AddVideoLinkUseCase { _, _, _ -> Result.failure(IllegalStateException()) },
        deleteVideo: DeleteVideoUseCase = DeleteVideoUseCase { Result.success(Unit) },
    ) = VideosSectionViewModel(
        getMyVideos = getMyVideos,
        selectVideoFile = selectVideoFile,
        uploadVideo = uploadVideo,
        addVideoLink = addVideoLink,
        deleteVideo = deleteVideo,
    )

    private fun TestScope.loadWithPickedVideo(viewModel: VideosSectionViewModel, title: String = "Mon clip") {
        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onVideoPicked("content://video/1")
        advanceUntilIdle()
        viewModel.addVideoForm.onTitleChange(title)
    }

    @Test
    fun `loadVideos should expose the streamer videos`() = runTest {
        val viewModel = createViewModel(getMyVideos = myVideos(video("v1", 1)))

        viewModel.loadVideos()
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(VideosSectionUiState.Success(videos = listOf(video("v1", 1))))
    }

    @Test
    fun `loadVideos failure should expose an error state`() = runTest {
        val viewModel = createViewModel(
            getMyVideos = { Result.failure(DomainException(DomainError.Network)) },
        )

        viewModel.loadVideos()
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(VideosSectionUiState.Error(CoreUiR.string.error_network))
    }

    @Test
    fun `three videos should disable adding a new one`() = runTest {
        val viewModel = createViewModel(getMyVideos = myVideos(video("v1", 1), video("v2", 2), video("v3", 3)))

        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onVideoPicked("content://video/9")
        advanceUntilIdle()

        assertThat((viewModel.state.value as VideosSectionUiState.Success).canAddVideo).isFalse()
        assertThat(viewModel.addVideoState.value.isVisible).isFalse()
    }

    @Test
    fun `a valid picked video should open the add form`() = runTest {
        val viewModel = createViewModel()

        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onVideoPicked("content://video/1")
        advanceUntilIdle()

        assertThat(viewModel.addVideoState.value.file).isEqualTo(pickedFile)
    }

    @Test
    fun `a rejected picked video should show the reason without opening the form`() = runTest {
        val viewModel = createViewModel(
            selectVideoFile = {
                Result.failure(InvalidVideoException(InvalidVideoException.Reason.FILE_TOO_LARGE))
            },
        )

        viewModel.events.test {
            viewModel.loadVideos()
            advanceUntilIdle()
            viewModel.onVideoPicked("content://video/1")
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                VideosSectionUiEvent.ShowMessage(UiText.Resource(R.string.videos_error_size)),
            )
        }
        assertThat(viewModel.addVideoState.value.isVisible).isFalse()
    }

    @Test
    fun `a completed upload should add the video and close the form`() = runTest {
        val created = video("new", 2)
        val viewModel = createViewModel(
            getMyVideos = myVideos(video("v1", 1)),
            uploadVideo = { _, _, _ ->
                flowOf(VideoUploadEvent.Progress(50), VideoUploadEvent.Completed(created))
            },
        )
        loadWithPickedVideo(viewModel)

        viewModel.onSubmitClick()
        advanceUntilIdle()

        assertThat((viewModel.state.value as VideosSectionUiState.Success).videos)
            .containsExactly(video("v1", 1), created).inOrder()
        assertThat(viewModel.addVideoState.value).isEqualTo(AddVideoUiState())
    }

    @Test
    fun `upload progress should be exposed while sending`() = runTest {
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ ->
                flow {
                    emit(VideoUploadEvent.Progress(42))
                    awaitCancellation()
                }
            },
        )
        loadWithPickedVideo(viewModel)

        viewModel.onSubmitClick()
        advanceUntilIdle()

        assertThat(viewModel.addVideoState.value.isUploading).isTrue()
        assertThat(viewModel.addVideoState.value.progressPercent).isEqualTo(42)
        viewModel.onCancelUploadClick()
    }

    @Test
    fun `a second click during an upload should not start another upload`() = runTest {
        var uploads = 0
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ ->
                uploads++
                flow<VideoUploadEvent> { awaitCancellation() }
            },
        )
        loadWithPickedVideo(viewModel)

        viewModel.onSubmitClick()
        viewModel.onSubmitClick()
        advanceUntilIdle()

        assertThat(uploads).isEqualTo(1)
        viewModel.onCancelUploadClick()
    }

    @Test
    fun `cancelling an upload should keep the form and say it was cancelled`() = runTest {
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ -> flow<VideoUploadEvent> { awaitCancellation() } },
        )
        loadWithPickedVideo(viewModel)
        viewModel.onSubmitClick()
        advanceUntilIdle()

        viewModel.onCancelUploadClick()
        advanceUntilIdle()

        val addState = viewModel.addVideoState.value
        assertThat(addState.isUploading).isFalse()
        assertThat(addState.file).isEqualTo(pickedFile)
        assertThat(addState.errorMessage).isEqualTo(UiText.Resource(R.string.videos_upload_cancelled))
    }

    @Test
    fun `a server rejection should show its message as is`() = runTest {
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ ->
                flow { throw DomainException(DomainError.Server(409, "Limite de 3 vidéos atteinte")) }
            },
        )
        loadWithPickedVideo(viewModel)

        viewModel.onSubmitClick()
        advanceUntilIdle()

        val addState = viewModel.addVideoState.value
        assertThat(addState.isUploading).isFalse()
        assertThat(addState.errorMessage).isEqualTo(UiText.Dynamic("Limite de 3 vidéos atteinte"))
    }

    @Test
    fun `no network during an upload should show the network message`() = runTest {
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ -> flow { throw DomainException(DomainError.Network) } },
        )
        loadWithPickedVideo(viewModel)

        viewModel.onSubmitClick()
        advanceUntilIdle()

        assertThat(viewModel.addVideoState.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_network))
    }

    @Test
    fun `an invalid title should keep the upload button disabled`() = runTest {
        var uploads = 0
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ ->
                uploads++
                flowOf()
            },
        )
        loadWithPickedVideo(viewModel, title = "a")

        viewModel.onSubmitClick()
        advanceUntilIdle()

        assertThat(viewModel.addVideoState.value.isSubmitEnabled).isFalse()
        assertThat(uploads).isEqualTo(0)
    }

    @Test
    fun `dismissing the form during an upload should be ignored`() = runTest {
        val viewModel = createViewModel(
            uploadVideo = { _, _, _ -> flow<VideoUploadEvent> { awaitCancellation() } },
        )
        loadWithPickedVideo(viewModel)
        viewModel.onSubmitClick()
        advanceUntilIdle()

        viewModel.addVideoForm.onDismiss()

        assertThat(viewModel.addVideoState.value.isVisible).isTrue()
        viewModel.onCancelUploadClick()
    }

    @Test
    fun `a confirmed deletion should remove the video`() = runTest {
        val viewModel = createViewModel(getMyVideos = myVideos(video("v1", 1), video("v2", 2)))
        viewModel.loadVideos()
        advanceUntilIdle()

        viewModel.onDeleteConfirm("v1")
        advanceUntilIdle()

        val state = viewModel.state.value as VideosSectionUiState.Success
        assertThat(state.videos).containsExactly(video("v2", 2))
        assertThat(state.deletingVideoIds).isEmpty()
    }

    @Test
    fun `a failed deletion should keep the video and show a message`() = runTest {
        val viewModel = createViewModel(
            getMyVideos = myVideos(video("v1", 1)),
            deleteVideo = { Result.failure(DomainException(DomainError.Network)) },
        )
        viewModel.loadVideos()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onDeleteConfirm("v1")
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                VideosSectionUiEvent.ShowMessage(UiText.Resource(CoreUiR.string.error_network)),
            )
        }
        assertThat((viewModel.state.value as VideosSectionUiState.Success).videos).containsExactly(video("v1", 1))
    }

    private fun TestScope.openLinkForm(viewModel: VideosSectionViewModel) {
        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onAddClick()
        viewModel.addVideoForm.onModeChange(AddVideoMode.LINK)
    }

    @Test
    fun `the add button should open an empty form on the file tab`() = runTest {
        val viewModel = createViewModel()

        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onAddClick()

        assertThat(viewModel.addVideoState.value).isEqualTo(AddVideoUiState(isOpen = true))
        assertThat(viewModel.addVideoState.value.isVisible).isTrue()
    }

    @Test
    fun `the add button should do nothing with three videos`() = runTest {
        val viewModel = createViewModel(getMyVideos = myVideos(video("v1", 1), video("v2", 2), video("v3", 3)))

        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onAddClick()

        assertThat(viewModel.addVideoState.value.isVisible).isFalse()
    }

    @Test
    fun `a link from another site should be flagged and keep the button disabled`() = runTest {
        val viewModel = createViewModel()

        openLinkForm(viewModel)
        viewModel.addVideoForm.onUrlChange("https://vimeo.com/123")
        viewModel.addVideoForm.onTitleChange("Mon clip")

        assertThat(viewModel.addVideoState.value.isLinkInvalid).isTrue()
        assertThat(viewModel.addVideoState.value.isSubmitEnabled).isFalse()
    }

    @Test
    fun `an added link should show progress then add the video and close the form`() = runTest {
        val linked = video("v9", 1).copy(url = "https://www.youtube.com/watch?v=abc")
        val sentLinks = mutableListOf<Triple<String, String, String>>()
        val viewModel = createViewModel(
            addVideoLink = { url, title, description ->
                sentLinks += Triple(url, title, description)
                Result.success(linked)
            },
        )

        viewModel.events.test {
            openLinkForm(viewModel)
            viewModel.addVideoForm.onUrlChange("https://youtu.be/abc")
            viewModel.addVideoForm.onTitleChange("Mon clip")
            viewModel.onSubmitClick()

            assertThat(viewModel.addVideoState.value.isUploading).isTrue()
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(VideosSectionUiEvent.ShowMessage(UiText.Resource(R.string.videos_added)))
        }
        assertThat(sentLinks).containsExactly(Triple("https://youtu.be/abc", "Mon clip", ""))
        assertThat(viewModel.addVideoState.value).isEqualTo(AddVideoUiState())
        assertThat((viewModel.state.value as VideosSectionUiState.Success).videos).containsExactly(linked)
    }

    @Test
    fun `a rejected link should show the server message as is`() = runTest {
        val viewModel = createViewModel(
            addVideoLink = { _, _, _ ->
                Result.failure(DomainException(DomainError.Server(409, "Tu as déjà 3 vidéos.")))
            },
        )

        openLinkForm(viewModel)
        viewModel.addVideoForm.onUrlChange("https://clips.twitch.tv/FunnyClip")
        viewModel.addVideoForm.onTitleChange("Mon clip")
        viewModel.onSubmitClick()
        advanceUntilIdle()

        assertThat(viewModel.addVideoState.value.isUploading).isFalse()
        assertThat(viewModel.addVideoState.value.errorMessage).isEqualTo(UiText.Dynamic("Tu as déjà 3 vidéos."))
        assertThat(viewModel.addVideoState.value.isVisible).isTrue()
    }

    @Test
    fun `switching tabs should be ignored while a link is being added`() = runTest {
        val viewModel = createViewModel(addVideoLink = { _, _, _ -> awaitCancellation() })

        openLinkForm(viewModel)
        viewModel.addVideoForm.onUrlChange("https://youtu.be/abc")
        viewModel.addVideoForm.onTitleChange("Mon clip")
        viewModel.onSubmitClick()
        viewModel.addVideoForm.onModeChange(AddVideoMode.FILE)

        assertThat(viewModel.addVideoState.value.mode).isEqualTo(AddVideoMode.LINK)
    }

    @Test
    fun `a rejected file picked from the open form should show the reason in the form`() = runTest {
        val viewModel = createViewModel(
            selectVideoFile = {
                Result.failure(InvalidVideoException(InvalidVideoException.Reason.UNSUPPORTED_FORMAT))
            },
        )

        viewModel.loadVideos()
        advanceUntilIdle()
        viewModel.onAddClick()
        viewModel.onVideoPicked("content://video/1")
        advanceUntilIdle()

        assertThat(viewModel.addVideoState.value.errorMessage)
            .isEqualTo(UiText.Resource(R.string.videos_error_format))
    }
}
