package com.strimup.core.streamer.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoOrientation
import org.junit.Test

class StreamerVideoMapperTest {

    private val apiBaseUrl = "https://api.strimup.test/"

    private fun dto(embed: StreamerDto.Embed?) = StreamerDto.Video(
        id = "video-1",
        title = "Mon clip",
        url = "https://www.tiktok.com/@strimup/video/735",
        description = null,
        order = 2,
        embed = embed,
    )

    @Test
    fun `a tiktok embed is vertical and has no thumbnail`() {
        val video = dto(StreamerDto.Embed("tiktok", "735", "video", "vertical")).toEntity(apiBaseUrl)

        assertThat(video.embed).isEqualTo(
            VideoEmbed(VideoEmbed.Provider.TIKTOK, "735", VideoEmbed.Kind.VIDEO, VideoOrientation.VERTICAL),
        )
        assertThat(video.thumbnailUrl).isNull()
    }

    @Test
    fun `a youtube short keeps its kind and gets the ytimg thumbnail`() {
        val video = dto(StreamerDto.Embed("youtube", "abc", "short", "vertical")).toEntity(apiBaseUrl)

        assertThat(video.embed?.kind).isEqualTo(VideoEmbed.Kind.SHORT)
        assertThat(video.thumbnailUrl).isEqualTo("https://i.ytimg.com/vi/abc/hqdefault.jpg")
    }

    @Test
    fun `a twitch clip gets the strimup thumbnail route`() {
        val video = dto(StreamerDto.Embed("twitch", "Slug", "clip", "horizontal")).toEntity(apiBaseUrl)

        assertThat(video.embed?.kind).isEqualTo(VideoEmbed.Kind.CLIP)
        assertThat(video.thumbnailUrl).isEqualTo("https://api.strimup.test/api/streamer/videos/video-1/thumbnail")
    }

    @Test
    fun `an uploaded file has no embed`() {
        val video = dto(embed = null).toEntity(apiBaseUrl)

        assertThat(video.embed).isNull()
        assertThat(video.thumbnailUrl).isNull()
        assertThat(video.description).isEmpty()
    }

    @Test
    fun `an unknown provider is ignored`() {
        val video = dto(StreamerDto.Embed("vimeo", "1", "video", "horizontal")).toEntity(apiBaseUrl)

        assertThat(video.embed).isNull()
    }

    @Test
    fun `a missing orientation falls back to horizontal`() {
        val video = dto(StreamerDto.Embed("youtube", "abc", null, null)).toEntity(apiBaseUrl)

        assertThat(video.embed?.orientation).isEqualTo(VideoOrientation.HORIZONTAL)
        assertThat(video.embed?.kind).isEqualTo(VideoEmbed.Kind.VIDEO)
    }
}
