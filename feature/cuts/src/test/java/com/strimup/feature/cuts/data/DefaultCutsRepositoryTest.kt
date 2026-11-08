package com.strimup.feature.cuts.data

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoOrientation
import com.strimup.feature.cuts.data.response.CutLikeResponse
import com.strimup.feature.cuts.data.response.CutsResponse
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.CutLike
import com.strimup.feature.cuts.domain.entity.CutUnavailableException
import com.strimup.feature.cuts.domain.entity.ExpiredCutsCursorException
import com.strimup.feature.cuts.domain.entity.LikeRateLimitedException
import com.strimup.feature.cuts.fake.FakeCutsApiService
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultCutsRepositoryTest {

    private val service = FakeCutsApiService()
    private val repository = DefaultCutsRepository(service)

    private fun httpException(code: Int, message: String? = null): HttpException {
        val body = message?.let { """{ "message": "$it" }""" } ?: ""
        return HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaTypeOrNull())))
    }

    private val youtubeItem = CutsResponse.Item(
        video = CutsResponse.Video(
            id = "video-1",
            title = "Mon meilleur moment",
            url = "https://youtube.com/shorts/abc",
            description = null,
            order = 1,
            embed = StreamerDto.Embed(provider = "youtube", id = "abc", kind = "short", orientation = "vertical"),
            likesCount = 42,
            likedByMe = true,
        ),
        streamer = CutsResponse.Streamer(
            id = "user-1",
            username = "Inox",
            avatarUrl = "https://cdn/avatar.png",
            isVerified = true,
            isLive = false,
        ),
    )

    @Test
    fun `getCuts should map the videos, their author and the next cursor`() = runTest {
        service.cutsResponse = { CutsResponse(items = listOf(youtubeItem), nextCursor = "next") }

        val page = repository.getCuts(cursor = null, limit = 10).getOrThrow()

        val cut = page.cuts.single()
        assertThat(page.nextCursor).isEqualTo("next")
        assertThat(cut.id).isEqualTo("video-1")
        assertThat(cut.video.description).isEmpty()
        assertThat(cut.video.embed).isEqualTo(
            VideoEmbed(VideoEmbed.Provider.YOUTUBE, "abc", VideoEmbed.Kind.SHORT, VideoOrientation.VERTICAL),
        )
        assertThat(cut.author).isEqualTo(Cut.Author("user-1", "Inox", "https://cdn/avatar.png", true, false))
        assertThat(cut.likesCount).isEqualTo(42)
        assertThat(cut.isLikedByMe).isTrue()
    }

    @Test
    fun `getCuts should send the cursor as is`() = runTest {
        repository.getCuts(cursor = "opaque", limit = 10)

        assertThat(service.requestedCursors).containsExactly("opaque")
    }

    @Test
    fun `an end of feed should have no next cursor`() = runTest {
        service.cutsResponse = { CutsResponse(items = listOf(youtubeItem), nextCursor = null) }

        assertThat(repository.getCuts(cursor = "opaque", limit = 10).getOrThrow().nextCursor).isNull()
    }

    @Test
    fun `a rejected cursor should be reported as expired`() = runTest {
        service.cutsResponse = { throw httpException(400, "Cursor invalide") }

        val error = repository.getCuts(cursor = "altered", limit = 10).exceptionOrNull()

        assertThat(error).isInstanceOf(ExpiredCutsCursorException::class.java)
    }

    @Test
    fun `a server error should keep its code`() = runTest {
        service.cutsResponse = { throw httpException(500, "Erreur interne du serveur") }

        val error = repository.getCuts(cursor = null, limit = 10).exceptionOrNull() as DomainException

        assertThat(error.error).isEqualTo(DomainError.Server(500, "Erreur interne du serveur"))
    }

    @Test
    fun `being offline should fail with a Network DomainError`() = runTest {
        service.cutsResponse = { throw IOException() }

        val error = repository.getCuts(cursor = null, limit = 10).exceptionOrNull() as DomainException

        assertThat(error.error).isEqualTo(DomainError.Network)
    }

    @Test
    fun `like and unlike should call the matching endpoint and return the server count`() = runTest {
        service.likeResponse = { CutLikeResponse(liked = false, likesCount = 41) }

        val result = repository.unlike("video-1").getOrThrow()
        repository.like("video-1")

        assertThat(result).isEqualTo(CutLike(isLiked = false, likesCount = 41))
        assertThat(service.likeCalls).containsExactly("video-1" to false, "video-1" to true).inOrder()
    }

    @Test
    fun `liking a missing video should report it as unavailable`() = runTest {
        service.likeResponse = { throw httpException(404, "Vidéo introuvable") }

        assertThat(repository.like("video-1").exceptionOrNull()).isInstanceOf(CutUnavailableException::class.java)
    }

    @Test
    fun `too many likes should keep the server message`() = runTest {
        service.likeResponse = { throw httpException(429, "Trop de likes, réessaie plus tard.") }

        val error = repository.like("video-1").exceptionOrNull() as LikeRateLimitedException

        assertThat(error.serverMessage).isEqualTo("Trop de likes, réessaie plus tard.")
    }

    @Test
    fun `an expired session should fail with Unauthorized`() = runTest {
        service.likeResponse = { throw httpException(401) }

        val error = repository.like("video-1").exceptionOrNull() as DomainException

        assertThat(error.error).isEqualTo(DomainError.Unauthorized)
    }
}
