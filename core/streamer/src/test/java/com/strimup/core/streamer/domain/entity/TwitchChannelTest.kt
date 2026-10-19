package com.strimup.core.streamer.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TwitchChannelTest {

    private fun streamerWith(vararg socials: Social) =
        Streamer(id = "1", userName = "Squeezie", imageUrl = null, socials = socials.toList())

    @Test
    fun `the channel should be read from the twitch link whatever its form`() {
        listOf(
            "https://www.twitch.tv/Squeezie",
            "https://twitch.tv/squeezie/",
            "http://m.twitch.tv/squeezie?lang=fr",
            "twitch.tv/squeezie",
            "  www.twitch.tv/@squeezie  ",
            "https://www.twitch.tv/squeezie/videos",
        ).forEach { url ->
            assertThat(url.toTwitchChannel()).isEqualTo("squeezie")
        }
    }

    @Test
    fun `a link that is not a twitch channel should give no channel`() {
        listOf(
            "",
            "https://www.youtube.com/@squeezie",
            "https://clips.twitch.tv/Kathish",
            "https://www.twitch.tv/",
            "https://www.twitch.tv/directory/category/just-chatting",
            "https://www.twitch.tv/videos/123456",
            "https://nottwitch.tv/squeezie",
            "https://www.twitch.tv/squeezie-fan",
        ).forEach { url ->
            assertThat(url.toTwitchChannel()).isNull()
        }
    }

    @Test
    fun `the streamer channel should come from its twitch social`() {
        val streamer = streamerWith(
            Social(url = "https://youtube.com/@squeezie", type = Social.Type.Youtube),
            Social(url = "https://www.twitch.tv/squeezie", type = Social.Type.Twitch),
        )

        assertThat(streamer.twitchChannel()).isEqualTo("squeezie")
    }

    @Test
    fun `a streamer without twitch link should have no channel`() {
        assertThat(streamerWith().twitchChannel()).isNull()
        assertThat(streamerWith(Social(url = null, type = Social.Type.Twitch)).twitchChannel()).isNull()
    }
}
