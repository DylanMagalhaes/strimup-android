package com.strimup.core.streamer.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.data.response.FilterOptionsResponse
import com.strimup.core.streamer.data.response.MatchedStreamerDto
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.data.response.StreamerMatchResponse
import com.strimup.core.streamer.data.response.UpdateAvatarResponse
import com.strimup.core.streamer.data.response.UpdateProfileResponse
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.tag.domain.entity.TagEntity
import org.junit.Test

class StreamerMapperTest {

    // StreamerDto.toEntity()

    @Test
    fun `toEntity should correctly map StreamerDto with custom isFavorite value`() {
        // GIVEN
        val isFavorite = true
        val streamerData = StreamerDto(
            id = "1",
            username = "Inox",
            avatarUrl = "https://example.com/avatar.png",
        )

        // WHEN
        val result = streamerData.toEntity(isFavorite)

        // THEN
        assertThat(result.id).isEqualTo("1")
        assertThat(result.userName).isEqualTo("Inox")
        assertThat(result.imageUrl).isEqualTo("https://example.com/avatar.png")
        assertThat(result.isFavorite).isEqualTo(isFavorite)
    }

    @Test
    fun `toEntity should map StreamerDto with null video descriptions to empty string`() {
        // GIVEN
        val streamerData = StreamerDto(
            id = "1",
            username = "Inox",
            avatarUrl = "https://example.com/avatar.png",
            videos = listOf(
                StreamerDto.Video(
                    id = "12",
                    title = "123",
                    url = "https://example.com/video.mp3",
                    description = null,
                    order = 1
                )
            )
        )

        // WHEN
        val result = streamerData.toEntity()

        // THEN
        assertThat(result.id).isEqualTo("1")
        assertThat(result.userName).isEqualTo("Inox")
        assertThat(result.imageUrl).isEqualTo("https://example.com/avatar.png")
        assertThat(result.videos.first().description).isEmpty()
    }

    @Test
    fun `toEntity should filter out unknown social types`() {
        // GIVEN
        val streamerData = StreamerDto(
            id = "1",
            username = "Inox",
            avatarUrl = "https://example.com/avatar.png",
            platforms = listOf(
                StreamerDto.Social(url = "https://twitch.tv/inox", type = "twitch"),
                StreamerDto.Social(url = "https://unknown.com/inox", type = "myspace")
            )
        )

        // WHEN
        val result = streamerData.toEntity()

        // THEN
        assertThat(result.socials).hasSize(1)
        assertThat(result.socials.first().type).isEqualTo(Social.Type.Twitch)
    }

    // StreamerDto.Social.toEntity()

    @Test
    fun `Social toEntity should correctly map known platform types`() {
        // GIVEN
        val socialDto = StreamerDto.Social(url = "https://youtube.com/inox", type = "yt")

        // WHEN
        val result = socialDto.toEntity()

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.type).isEqualTo(Social.Type.Youtube)
        assertThat(result?.url).isEqualTo("https://youtube.com/inox")
    }

    @Test
    fun `Social toEntity should return null for unknown platform type`() {
        // GIVEN
        val socialDto = StreamerDto.Social(url = "https://unknown.com", type = "unknown_platform")

        // WHEN
        val result = socialDto.toEntity()

        // THEN
        assertThat(result).isNull()
    }

    // MatchedStreamerDto.toDomain()

    @Test
    fun `MatchedStreamerDto toDomain should map streamer and construct socials list from non-null URLs`() {
        // GIVEN
        val dto = MatchedStreamerDto(
            id = "1",
            username = "Inox",
            imageUrl = "https://example.com/avatar.png",
            isLive = true,
            liveTitle = "Live chill",
            twitchUrl = "https://twitch.tv/inox",
            youtubeUrl = null,
            instagramUrl = "https://instagram.com/inox",
            tiktokUrl = null,
            kickUrl = null
        )

        // WHEN
        val domain = dto.toDomain()

        // THEN
        assertThat(domain.id).isEqualTo("1")
        assertThat(domain.userName).isEqualTo("Inox")
        assertThat(domain.isLive).isTrue()
        assertThat(domain.socials).hasSize(2)
        assertThat(domain.socials.map { it.type }).containsExactly(
            Social.Type.Twitch,
            Social.Type.Instagram
        )
    }

    // StreamerMatchResponse.toDomain()

    @Test
    fun `StreamerMatchResponse toDomain should map total count and list of matched streamers`() {
        // GIVEN
        val response = StreamerMatchResponse(
            total = 1,
            matchedStreamers = listOf(
                MatchedStreamerDto(
                    id = "1",
                    username = "Inox",
                    imageUrl = "https://example.com/avatar.png",
                    isLive = false,
                    liveTitle = null,
                    twitchUrl = "https://twitch.tv/inox",
                    youtubeUrl = null,
                    instagramUrl = null,
                    tiktokUrl = null,
                    kickUrl = null
                )
            )
        )

        // WHEN
        val domain = response.toDomain()

        // THEN
        assertThat(domain.total).isEqualTo(1)
        assertThat(domain.streamers).hasSize(1)
        assertThat(domain.streamers.first().userName).isEqualTo("Inox")
    }

    // UpdateProfileResponse.Streamer.toEntity()

    @Test
    fun `UpdateProfileResponse Streamer toEntity should correctly map response to domain Streamer`() {
        // GIVEN
        val updateStreamer = UpdateProfileResponse.Streamer(
            id = "1",
            avatarUrl = "https://example.com/avatar.png",
            isLive = false,
            liveTitle = null,
            bio = "Nouvelle bio",
            dailyStatus = "En forme",
            averageViewers = "100-500",
            languages = listOf("FR"),
            personality = "Chill",
            personalitySecondary = "Tryhard",
            streamFrequency = "DAILY",
            twitchUrl = "https://twitch.tv/inox",
            youtubeUrl = null,
            instagramUrl = null,
            tiktokUrl = null,
            kickUrl = null,
            tags = listOf(
                UpdateProfileResponse.Streamer.TagDto(
                    id = 10,
                    name = "Gaming",
                    category = "GAME"
                )
            ),
            userId = "12",
            )

        // WHEN
        val result = updateStreamer.toEntity()

        // THEN
        assertThat(result.id).isEqualTo("1")
        assertThat(result.bio).isEqualTo("Nouvelle bio")
        assertThat(result.socials).hasSize(1)
        assertThat(result.tags).hasSize(1)
        assertThat(result.tags?.first()?.name).isEqualTo("Gaming")
    }

    // Streamer.toUpdateProfileRequest()

    @Test
    fun `toUpdateProfileRequest should correctly extract social URLs and map streamer properties`() {
        // GIVEN
        val streamer = Streamer(
            id = "1",
            userName = "Inox",
            imageUrl = "https://example.com/avatar.png",
            socials = listOf(
                Social(url = "https://twitch.tv/inox", type = Social.Type.Twitch),
                Social(url = "https://youtube.com/inox", type = Social.Type.Youtube)
            ),
            bio = "Ma bio",
            dailyStatus = "Chill",
            tags = listOf(TagEntity(id = 14, name = "Gaming", category = "GAME")),
            averageViewers = "100-500",
            languages = listOf("FR", "EN"),
            personality = "Fun",
            personalitySecondary = "Tryhard",
            streamFrequency = "WEEKLY"
        )

        // WHEN
        val request = streamer.toUpdateProfileRequest()

        // THEN
        assertThat(request.bio).isEqualTo("Ma bio")
        assertThat(request.twitchUrl).isEqualTo("https://twitch.tv/inox")
        assertThat(request.youtubeUrl).isEqualTo("https://youtube.com/inox")
        assertThat(request.instagramUrl).isNull()
        assertThat(request.tags).containsExactly(14)
        assertThat(request.languages).containsExactly("FR", "EN")
    }

    @Test
    fun `toUpdateProfileRequest should trim blank strings and empty lists`() {
        // GIVEN
        val streamer = Streamer(
            id = "1",
            userName = "Inox",
            imageUrl = "",
            socials = listOf(
                Social(url = "   ", type = Social.Type.Twitch)
            ),
            bio = "   ",
            dailyStatus = "",
            languages = listOf("FR", "   ")
        )

        // WHEN
        val request = streamer.toUpdateProfileRequest()

        // THEN
        assertThat(request.bio).isNull()
        assertThat(request.dailyStatus).isNull()
        assertThat(request.twitchUrl).isNull()
        assertThat(request.languages).containsExactly("FR")
    }

    // --- Streamer.toFavoriteRoom() ---

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
        assertThat(favoriteRoom.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    // --- FilterOptionsResponse.toEntity() ---

    @Test
    fun `FilterOptionsResponse toEntity should correctly map to StreamerOptions`() {
        // GIVEN
        val response = FilterOptionsResponse(
            averageViewers = listOf("0-50", "50-100"),
            languages = listOf("FR", "EN"),
            personalities = listOf("Tryhard", "Chill"),
            streamFrequencies = listOf("DAILY", "WEEKLY")
        )

        // WHEN
        val options = response.toEntity()

        // THEN
        assertThat(options.averageViewers).containsExactly("0-50", "50-100")
        assertThat(options.languages).containsExactly("FR", "EN")
        assertThat(options.personalities).containsExactly("Tryhard", "Chill")
        assertThat(options.streamFrequencies).containsExactly("DAILY", "WEEKLY")
    }

    // --- UpdateAvatarResponse.toDomain() ---

    @Test
    fun `UpdateAvatarResponse toDomain should return avatarUrl string`() {
        // GIVEN
        val response = UpdateAvatarResponse(
            avatarUrl = "https://example.com/new_avatar.png",
            message = ""
        )

        // WHEN
        val avatarUrl = response.toDomain()

        // THEN
        assertThat(avatarUrl).isEqualTo("https://example.com/new_avatar.png")
    }
}