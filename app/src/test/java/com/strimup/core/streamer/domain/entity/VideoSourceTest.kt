package com.strimup.core.streamer.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VideoSourceTest {

    private fun sourceOf(url: String) =
        Streamer.Video(id = "1", title = "Clip", description = "", url = url, order = 1).source()

    @Test
    fun `an uploaded mp4 mov or webm file should be played in the app`() {
        val mp4 = "https://pub-c72a.r2.dev/profile-videos/214453c3.mp4"

        assertThat(sourceOf(mp4)).isEqualTo(VideoSource.HostedFile(mp4))
        assertThat(sourceOf("https://cdn.strimup.com/v/clip.MOV")).isInstanceOf(VideoSource.HostedFile::class.java)
        assertThat(sourceOf("https://cdn.strimup.com/v/clip.webm?token=1"))
            .isInstanceOf(VideoSource.HostedFile::class.java)
    }

    @Test
    fun `youtube links should stay external including shorts and lives`() {
        listOf(
            "https://youtu.be/skbguu1xSAM?si=x",
            "https://youtube.com/shorts/kufQRD54EwA",
            "https://www.youtube.com/live/zfLGyyNKYxw",
            "https://m.youtube.com/watch?v=iUwV1ASTCfg",
        ).forEach { url ->
            val link = sourceOf(url) as VideoSource.ExternalLink
            assertThat(link.platform).isEqualTo(VideoSource.Platform.YOUTUBE)
        }
    }

    @Test
    fun `other platforms should be recognized`() {
        assertThat((sourceOf("https://vm.tiktok.com/ZNRxHAMDM/") as VideoSource.ExternalLink).platform)
            .isEqualTo(VideoSource.Platform.TIKTOK)
        assertThat((sourceOf("https://clips.twitch.tv/Kathish") as VideoSource.ExternalLink).platform)
            .isEqualTo(VideoSource.Platform.TWITCH)
        assertThat((sourceOf("https://www.instagram.com/reel/DJCU/") as VideoSource.ExternalLink).platform)
            .isEqualTo(VideoSource.Platform.INSTAGRAM)
        val canva = "https://canva.link/pl39llu6e7kfwrs"
        assertThat(sourceOf(canva))
            .isEqualTo(
                VideoSource.ExternalLink(url = canva, host = "canva.link", platform = VideoSource.Platform.OTHER),
            )
    }

    @Test
    fun `an empty text or not web url should be unavailable`() {
        assertThat(sourceOf("")).isEqualTo(VideoSource.Unavailable)
        assertThat(sourceOf("Regardez cette vidéo : « Fortnite » https://youtu.be/abc"))
            .isEqualTo(VideoSource.Unavailable)
        assertThat(sourceOf("javascript:alert(1)")).isEqualTo(VideoSource.Unavailable)
        assertThat(sourceOf("ftp://files.example.com/clip.mp4")).isEqualTo(VideoSource.Unavailable)
    }
}
