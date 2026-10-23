package com.strimup.core.streamer.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VideoEmbedUrlsTest {

    private fun embed(provider: VideoEmbed.Provider, id: String, kind: VideoEmbed.Kind = VideoEmbed.Kind.VIDEO) =
        VideoEmbed(provider = provider, id = id, kind = kind, orientation = VideoOrientation.HORIZONTAL)

    @Test
    fun `youtube player uses the privacy enhanced domain`() {
        val url = embed(VideoEmbed.Provider.YOUTUBE, "abc123").playerUrl(autoplay = true)

        assertThat(url).isEqualTo("https://www.youtube-nocookie.com/embed/abc123?autoplay=1&rel=0&playsinline=1")
    }

    @Test
    fun `youtube player without autoplay`() {
        val url = embed(VideoEmbed.Provider.YOUTUBE, "abc123", VideoEmbed.Kind.SHORT).playerUrl(autoplay = false)

        assertThat(url).isEqualTo("https://www.youtube-nocookie.com/embed/abc123?autoplay=0&rel=0&playsinline=1")
    }

    @Test
    fun `tiktok player uses the official v1 player`() {
        val url = embed(VideoEmbed.Provider.TIKTOK, "7350000000000000000").playerUrl(autoplay = true)

        assertThat(url).isEqualTo("https://www.tiktok.com/player/v1/7350000000000000000?autoplay=1&rel=0")
    }

    @Test
    fun `twitch clip uses the clips embed with the parent domain`() {
        val url = embed(VideoEmbed.Provider.TWITCH, "FunnyClipSlug", VideoEmbed.Kind.CLIP)
            .playerUrl(autoplay = true, parentDomain = "www.strimup.com")

        assertThat(url).isEqualTo(
            "https://clips.twitch.tv/embed?clip=FunnyClipSlug&parent=www.strimup.com&autoplay=true",
        )
    }

    @Test
    fun `twitch vod uses the video player with the default parent domain`() {
        val url = embed(VideoEmbed.Provider.TWITCH, "2100000000", VideoEmbed.Kind.VOD).playerUrl(autoplay = false)

        assertThat(url).isEqualTo("https://player.twitch.tv/?video=2100000000&parent=strimup.com&autoplay=false")
    }

    @Test
    fun `youtube thumbnail comes from ytimg`() {
        val url = embed(VideoEmbed.Provider.YOUTUBE, "abc123").thumbnailUrl(videoId = "v1", apiBaseUrl = "https://api/")

        assertThat(url).isEqualTo("https://i.ytimg.com/vi/abc123/hqdefault.jpg")
    }

    @Test
    fun `twitch thumbnail goes through the strimup thumbnail route`() {
        val url = embed(VideoEmbed.Provider.TWITCH, "slug", VideoEmbed.Kind.CLIP)
            .thumbnailUrl(videoId = "video-uuid", apiBaseUrl = "https://strimup-back.onrender.com/")

        assertThat(url).isEqualTo("https://strimup-back.onrender.com/api/streamer/videos/video-uuid/thumbnail")
    }

    @Test
    fun `tiktok has no thumbnail`() {
        val url = embed(VideoEmbed.Provider.TIKTOK, "735").thumbnailUrl(videoId = "v1", apiBaseUrl = "https://api/")

        assertThat(url).isNull()
    }
}
