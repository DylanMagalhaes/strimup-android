package com.strimup.feature.streamervideos.data

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.data.file.VideoFileReader
import com.strimup.feature.streamervideos.data.response.AddStreamerVideoResponse
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideo
import com.strimup.feature.streamervideos.domain.entity.VideoFormat
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.io.InputStream

class DefaultStreamerVideoRepositoryTest {

    private val uploadedVideo = StreamerDto.Video(
        id = "v1",
        title = "Mon clip",
        url = "https://cdn.strimup.com/videos/v1.mp4",
        description = null,
        order = 1,
    )

    private val localFile = LocalVideoFile(
        uri = "content://media/video/7",
        fileName = "clip.mp4",
        mimeType = "video/mp4",
        sizeBytes = 4_096L,
    )

    private class FakeVideoFileReader(private val bytes: ByteArray = ByteArray(4_096)) : VideoFileReader {
        var describedUri: String? = null

        override suspend fun describe(uri: String): LocalVideoFile {
            describedUri = uri
            return LocalVideoFile(uri = uri, fileName = "clip.mov", mimeType = "video/quicktime", sizeBytes = 12L)
        }

        override fun open(uri: String): InputStream = bytes.inputStream()
    }

    private class FakeStreamerVideoApiService(
        private val myVideos: () -> List<StreamerDto.Video> = { emptyList() },
        private val addResult: () -> AddStreamerVideoResponse = { error("unused") },
    ) : StreamerVideoApiService {
        val sentParts = mutableListOf<MultipartBody.Part>()
        val deletedIds = mutableListOf<String>()

        override suspend fun getMyVideos(): List<StreamerDto.Video> = myVideos()

        override suspend fun addVideo(
            video: MultipartBody.Part,
            title: MultipartBody.Part,
            description: MultipartBody.Part?,
        ): AddStreamerVideoResponse {
            sentParts += listOfNotNull(video, title, description)
            video.body.writeTo(Buffer())
            return addResult()
        }

        override suspend fun deleteVideo(id: String) {
            deletedIds += id
        }
    }

    private fun MultipartBody.Part.disposition(): String? = headers?.get("Content-Disposition")

    private fun MultipartBody.Part.text(): String = Buffer().also { body.writeTo(it) }.readUtf8()

    @Test
    fun `getMyVideos should map the videos sorted by order`() = runTest {
        val repository = DefaultStreamerVideoRepository(
            FakeStreamerVideoApiService(
                myVideos = { listOf(uploadedVideo.copy(id = "v2", order = 2), uploadedVideo) },
            ),
            FakeVideoFileReader(),
        )

        val result = repository.getMyVideos()

        assertThat(result.getOrThrow().map { it.id }).containsExactly("v1", "v2").inOrder()
        assertThat(result.getOrThrow().first().description).isEmpty()
    }

    @Test
    fun `getMyVideos should map a network failure to a DomainError`() = runTest {
        val repository = DefaultStreamerVideoRepository(
            FakeStreamerVideoApiService(myVideos = { throw IOException() }),
            FakeVideoFileReader(),
        )

        val result = repository.getMyVideos()

        assertThat((result.exceptionOrNull() as DomainException).error).isEqualTo(DomainError.Network)
    }

    @Test
    fun `getLocalVideoFile should describe the picked uri`() = runTest {
        val reader = FakeVideoFileReader()
        val repository = DefaultStreamerVideoRepository(FakeStreamerVideoApiService(), reader)

        val result = repository.getLocalVideoFile("content://media/video/9")

        assertThat(reader.describedUri).isEqualTo("content://media/video/9")
        assertThat(result.getOrThrow().mimeType).isEqualTo("video/quicktime")
    }

    @Test
    fun `uploadVideo should emit progress then the created video`() = runTest {
        val repository = DefaultStreamerVideoRepository(
            FakeStreamerVideoApiService(addResult = { AddStreamerVideoResponse(video = uploadedVideo) }),
            FakeVideoFileReader(),
        )

        repository.uploadVideo(
            NewStreamerVideo(file = localFile, format = VideoFormat.MP4, title = "Mon clip", description = null),
        ).toList().let { events ->
            assertThat(events.filterIsInstance<VideoUploadEvent.Progress>().last().percent).isEqualTo(100)
            assertThat(events.last()).isEqualTo(
                VideoUploadEvent.Completed(
                    Streamer.Video(
                        id = "v1",
                        title = "Mon clip",
                        description = "",
                        url = "https://cdn.strimup.com/videos/v1.mp4",
                        order = 1,
                    ),
                ),
            )
        }
    }

    @Test
    fun `uploadVideo should send the file title and description as multipart parts`() = runTest {
        val service = FakeStreamerVideoApiService(addResult = { AddStreamerVideoResponse(video = uploadedVideo) })
        val repository = DefaultStreamerVideoRepository(service, FakeVideoFileReader())

        repository.uploadVideo(
            NewStreamerVideo(file = localFile, format = VideoFormat.MOV, title = "Mon clip", description = "Best of"),
        ).toList()

        val (video, title, description) = service.sentParts
        assertThat(video.disposition()).isEqualTo("form-data; name=\"video\"; filename=\"video.mov\"")
        assertThat(video.body.contentType().toString()).isEqualTo("video/quicktime")
        assertThat(title.disposition()).isEqualTo("form-data; name=\"title\"")
        assertThat(title.text()).isEqualTo("Mon clip")
        assertThat(description.text()).isEqualTo("Best of")
    }

    @Test
    fun `uploadVideo should not send an empty description part`() = runTest {
        val service = FakeStreamerVideoApiService(addResult = { AddStreamerVideoResponse(video = uploadedVideo) })
        val repository = DefaultStreamerVideoRepository(service, FakeVideoFileReader())

        repository.uploadVideo(
            NewStreamerVideo(file = localFile, format = VideoFormat.MP4, title = "Mon clip", description = null),
        ).toList()

        assertThat(service.sentParts).hasSize(2)
    }

    @Test
    fun `uploadVideo should expose the server message of a rejected upload`() = runTest {
        val rejection = HttpException(
            Response.error<Any>(409, """{"message":"Limite de 3 vidéos atteinte"}""".toResponseBody()),
        )
        val repository = DefaultStreamerVideoRepository(
            FakeStreamerVideoApiService(addResult = { throw rejection }),
            FakeVideoFileReader(),
        )

        val error = repository.uploadVideo(
            NewStreamerVideo(file = localFile, format = VideoFormat.MP4, title = "Mon clip", description = null),
        ).let { flow -> runCatching { flow.toList() }.exceptionOrNull() }

        assertThat((error as DomainException).error)
            .isEqualTo(DomainError.Server(409, "Limite de 3 vidéos atteinte"))
    }

    @Test
    fun `deleteVideo should call the api with the video id`() = runTest {
        val service = FakeStreamerVideoApiService()
        val repository = DefaultStreamerVideoRepository(service, FakeVideoFileReader())

        val result = repository.deleteVideo("v1")

        assertThat(result.isSuccess).isTrue()
        assertThat(service.deletedIds).containsExactly("v1")
    }
}
