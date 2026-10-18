package com.strimup.feature.home.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.database.model.HomeSocialRoomModel
import com.strimup.core.database.model.HomeStreamerRoomEntity
import org.junit.Test

class HomeStreamerMapperTest {

    @Test
    fun `unknown social types should be skipped instead of crashing`() {
        val entity = HomeStreamerRoomEntity(
            id = "1",
            orderIndex = 0,
            userName = "Inox",
            imageUrl = null,
            socials = listOf(
                HomeSocialRoomModel(url = "https://twitch.tv/inox", type = "Twitch"),
                HomeSocialRoomModel(url = "https://unknown", type = "BlueSky"),
            ),
            tags = emptyList(),
            personality = null,
            personalitySecondary = null,
        )

        val streamer = entity.toStreamer()

        assertThat(streamer.socials.map { it.type.name }).containsExactly("Twitch")
    }
}
