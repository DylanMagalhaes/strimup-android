package com.strimup.core.favorite.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import org.junit.Test

class FavoriteStreamerMapperTest {

    @Test
    fun `toFavoriteRoom should correctly map Streamer to FavoriteRoomEntity`() {
        // GIVEN
        val streamer = Streamer(
            id = "1",
            userName = "Inox",
            imageUrl = "https://example.com/avatar.png"
        )

        // WHEN
        val favoriteRoom = streamer.toFavoriteRoom()

        // THEN
        assertThat(favoriteRoom.id).isEqualTo("1")
        assertThat(favoriteRoom.userName).isEqualTo("Inox")
        assertThat(favoriteRoom.imageUrl).isEqualTo("https://example.com/avatar.png")
    }
}
