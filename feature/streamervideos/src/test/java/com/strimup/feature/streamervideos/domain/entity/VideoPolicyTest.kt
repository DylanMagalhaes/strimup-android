package com.strimup.feature.streamervideos.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VideoPolicyTest {

    private fun file(fileName: String? = "clip.mp4", mimeType: String? = "video/mp4", sizeBytes: Long? = 1_000L) =
        LocalVideoFile(uri = "content://video/1", fileName = fileName, mimeType = mimeType, sizeBytes = sizeBytes)

    @Test
    fun `formatOf should use the mime type first`() {
        assertThat(VideoPolicy.formatOf(file(fileName = "clip.bin", mimeType = "video/quicktime")))
            .isEqualTo(VideoFormat.MOV)
    }

    @Test
    fun `formatOf should fall back on the file extension`() {
        assertThat(VideoPolicy.formatOf(file(fileName = "clip.WEBM", mimeType = null))).isEqualTo(VideoFormat.WEBM)
    }

    @Test
    fun `formatOf should reject other video formats`() {
        assertThat(VideoPolicy.formatOf(file(fileName = "clip.avi", mimeType = "video/x-msvideo"))).isNull()
    }

    @Test
    fun `isTooLarge should accept exactly 80 MB and reject more`() {
        assertThat(VideoPolicy.isTooLarge(file(sizeBytes = 80L * 1024 * 1024))).isFalse()
        assertThat(VideoPolicy.isTooLarge(file(sizeBytes = 80L * 1024 * 1024 + 1))).isTrue()
    }

    @Test
    fun `isTooLarge should let an unknown size through`() {
        assertThat(VideoPolicy.isTooLarge(file(sizeBytes = null))).isFalse()
    }

    @Test
    fun `isValidTitle should require 2 to 120 trimmed characters`() {
        assertThat(VideoPolicy.isValidTitle(" a ")).isFalse()
        assertThat(VideoPolicy.isValidTitle("ab")).isTrue()
        assertThat(VideoPolicy.isValidTitle("a".repeat(120))).isTrue()
        assertThat(VideoPolicy.isValidTitle("a".repeat(121))).isFalse()
    }

    @Test
    fun `isValidDescription should allow no description and at most 1000 characters`() {
        assertThat(VideoPolicy.isValidDescription(null)).isTrue()
        assertThat(VideoPolicy.isValidDescription("a".repeat(1000))).isTrue()
        assertThat(VideoPolicy.isValidDescription("a".repeat(1001))).isFalse()
    }

    @Test
    fun `isSupportedLink should accept youtube tiktok and twitch links`() {
        listOf(
            "https://www.youtube.com/watch?v=abc123",
            "https://youtu.be/abc123",
            "https://m.youtube.com/shorts/abc123",
            "https://www.tiktok.com/@strimup/video/7350000000000000000",
            "https://vm.tiktok.com/ZMabc123/",
            "https://www.twitch.tv/videos/2100000000",
            "https://clips.twitch.tv/FunnyClipSlug",
            "youtu.be/abc123",
        ).forEach { url -> assertThat(VideoPolicy.isSupportedLink(url)).isTrue() }
    }

    @Test
    fun `isSupportedLink should reject other domains and lookalikes`() {
        listOf(
            "",
            "https://vimeo.com/123",
            "https://notyoutube.com/watch?v=abc",
            "https://youtube.com.evil.example/watch?v=abc",
            "ftp://youtube.com/watch?v=abc",
            "pas un lien",
        ).forEach { url -> assertThat(VideoPolicy.isSupportedLink(url)).isFalse() }
    }

    @Test
    fun `isSupportedLink should reject links longer than 500 characters`() {
        val url = "https://youtube.com/watch?v=" + "a".repeat(VideoPolicy.MAX_LINK_LENGTH)

        assertThat(VideoPolicy.isSupportedLink(url)).isFalse()
    }

    @Test
    fun `normalizeLink should add https when the scheme is missing`() {
        assertThat(VideoPolicy.normalizeLink("  youtu.be/abc ")).isEqualTo("https://youtu.be/abc")
        assertThat(VideoPolicy.normalizeLink("http://youtu.be/abc")).isEqualTo("http://youtu.be/abc")
    }
}
