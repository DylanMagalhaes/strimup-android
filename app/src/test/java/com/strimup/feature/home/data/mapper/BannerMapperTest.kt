package com.strimup.feature.home.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.home.data.response.BannerItemsResponse
import org.junit.Test

class BannerMapperTest {

    @Test
    fun `should map all fields when streamer is present`() {
        // GIVEN
        val response = bannerResponse(
            type = "FEATURED_STREAMER",
            title = "Streamer de la semaine",
            description = "Decouvre raziu",
            imageUrl = "https://cdn/banner.png",
            position = 2,
            linkUrl = "https://strimup.com/raziu",
            streamer = BannerItemsResponse.Streamer(
                id = "s-1",
                avatarUrl = "https://cdn/avatar.png"
            ),
        )

        // WHEN
        val actual = response.toDomain()

        // THEN
        assertThat(actual.type).isEqualTo("FEATURED_STREAMER")
        assertThat(actual.title).isEqualTo("Streamer de la semaine")
        assertThat(actual.description).isEqualTo("Decouvre raziu")
        assertThat(actual.imageUrl).isEqualTo("https://cdn/banner.png")
        assertThat(actual.position).isEqualTo(2)
        assertThat(actual.linkUrl).isEqualTo("https://strimup.com/raziu")
        assertThat(actual.avatarUrl).isEqualTo("https://cdn/avatar.png")
        assertThat(actual.streamerId).isEqualTo("s-1")
    }

    @Test
    fun `should map avatarUrl and streamerId to null when streamer is absent`() {
        // GIVEN
        val response = bannerResponse(streamer = null)

        // WHEN
        val entity = response.toDomain()

        // THEN
        assertThat(entity.avatarUrl).isNull()
        assertThat(entity.streamerId).isNull()
    }

    @Test
    fun `should fallback to empty string when imageUrl is null`() {
        // GIVEN
        val response = bannerResponse(imageUrl = null)

        // WHEN
        val entity = response.toDomain()

        // THEN
        assertThat(entity.imageUrl).isEqualTo("")
    }

    @Test
    fun `should handle streamer present with null fields`() {
        // GIVEN
        val response = bannerResponse(
            streamer = BannerItemsResponse.Streamer(id = null, avatarUrl = null),
        )

        // WHEN
        val entity = response.toDomain()

        // THEN
        assertThat(entity.avatarUrl).isNull()
        assertThat(entity.streamerId).isNull()
    }

    private fun bannerResponse(
        type: String = "PROMO",
        title: String = "Titre",
        description: String = "Description",
        imageUrl: String? = "https://cdn/default.png",
        position: Int = 0,
        linkUrl: String = "https://strimup.com",
        streamer: BannerItemsResponse.Streamer? = null,
    ) = BannerItemsResponse(
        type = type,
        title = title,
        description = description,
        imageUrl = imageUrl,
        position = position,
        linkUrl = linkUrl,
        streamer = streamer,
    )
}