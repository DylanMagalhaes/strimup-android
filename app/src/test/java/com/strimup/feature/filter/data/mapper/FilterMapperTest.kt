package com.strimup.feature.filter.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.data.request.FilterJsonDto
import com.strimup.core.streamer.data.request.TagDto
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.feature.filter.data.local.model.FilterRoomEntity
import com.strimup.feature.filter.data.local.model.TagRoomModel
import com.strimup.feature.filter.data.response.FilterResponse
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import org.junit.Test

class FilterMapperTest {

    // region FilterResponse.toDomain()

    @Test
    fun `FilterResponse toDomain should map id, name, userId and the mapped criteria`() {
        // GIVEN
        val response = FilterResponse(
            id = "f1",
            name = "Chill",
            userId = "u1",
            filterJson = FilterJsonDto(ageRange = listOf(20, 30), status = "ACTIVE"),
        )

        // WHEN
        val result = response.toDomain()

        // THEN
        assertThat(result.id).isEqualTo("f1")
        assertThat(result.name).isEqualTo("Chill")
        assertThat(result.userId).isEqualTo("u1")
        assertThat(result.criteria.ageRange).isEqualTo(20..30)
        assertThat(result.criteria.status).isEqualTo("ACTIVE")
    }

    // endregion

    // region FilterJsonDto.toDomain()

    @Test
    fun `FilterJsonDto toDomain should use the provided ageRange when it has at least 2 values`() {
        val dto = FilterJsonDto(ageRange = listOf(25, 40))
        assertThat(dto.toDomain().ageRange).isEqualTo(25..40)
    }

    @Test
    fun `FilterJsonDto toDomain should default ageRange to 18 to 80 when null`() {
        val dto = FilterJsonDto(ageRange = null)
        assertThat(dto.toDomain().ageRange).isEqualTo(18..80)
    }

    @Test
    fun `FilterJsonDto toDomain should default ageRange to 18 to 80 when it has fewer than 2 values`() {
        val dto = FilterJsonDto(ageRange = listOf(25))
        assertThat(dto.toDomain().ageRange).isEqualTo(18..80)
    }

    @Test
    fun `FilterJsonDto toDomain should default null lists to empty lists`() {
        // GIVEN
        val dto = FilterJsonDto(
            languages = null,
            platforms = null,
            personalities = null,
            tags = null,
        )

        // WHEN
        val result = dto.toDomain()

        // THEN
        assertThat(result.languages).isEmpty()
        assertThat(result.platforms).isEmpty()
        assertThat(result.personalities).isEmpty()
        assertThat(result.tags).isEmpty()
    }

    @Test
    fun `FilterJsonDto toDomain should default null strings to empty strings`() {
        // GIVEN
        val dto = FilterJsonDto(averageViewers = null, streamFrequency = null, status = null)

        // WHEN
        val result = dto.toDomain()

        // THEN
        assertThat(result.averageViewers).isEmpty()
        assertThat(result.streamFrequency).isEmpty()
        assertThat(result.status).isEmpty()
    }

    @Test
    fun `FilterJsonDto toDomain should map the provided tags`() {
        // GIVEN
        val dto = FilterJsonDto(tags = listOf(TagDto(id = 1, name = "FPS", category = "Gaming")))

        // WHEN
        val result = dto.toDomain()

        // THEN
        assertThat(result.tags).containsExactly(TagEntity(id = 1, name = "FPS", category = "Gaming"))
    }

    // endregion

    // region FilterCriteria.toDto()

    @Test
    fun `FilterCriteria toDto should convert ageRange to a two element list`() {
        val criteria = FilterCriteria(ageRange = 21..35)
        assertThat(criteria.toDto().ageRange).isEqualTo(listOf(21, 35))
    }

    @Test
    fun `FilterCriteria toDto should convert empty lists to null`() {
        // GIVEN
        val criteria = FilterCriteria(
            languages = emptyList(),
            platforms = emptyList(),
            personalities = emptyList(),
            tags = emptyList(),
        )

        // WHEN
        val dto = criteria.toDto()

        // THEN
        assertThat(dto.languages).isNull()
        assertThat(dto.platforms).isNull()
        assertThat(dto.personalities).isNull()
        assertThat(dto.tags).isNull()
    }

    @Test
    fun `FilterCriteria toDto should keep non-empty lists`() {
        // GIVEN
        val criteria = FilterCriteria(
            languages = listOf("FR"),
            tags = listOf(TagEntity(id = 1, name = "FPS", category = "Gaming")),
        )

        // WHEN
        val dto = criteria.toDto()

        // THEN
        assertThat(dto.languages).containsExactly("FR")
        assertThat(dto.tags).containsExactly(TagDto(id = 1, name = "FPS", category = "Gaming"))
    }

    @Test
    fun `FilterCriteria toDto should convert blank strings to null`() {
        // GIVEN
        val criteria = FilterCriteria(averageViewers = "", streamFrequency = "", status = "")

        // WHEN
        val dto = criteria.toDto()

        // THEN
        assertThat(dto.averageViewers).isNull()
        assertThat(dto.streamFrequency).isNull()
        assertThat(dto.status).isNull()
    }

    @Test
    fun `FilterCriteria toDto should keep non-blank strings`() {
        // GIVEN
        val criteria = FilterCriteria(averageViewers = "0-50", streamFrequency = "DAILY", status = "ACTIVE")

        // WHEN
        val dto = criteria.toDto()

        // THEN
        assertThat(dto.averageViewers).isEqualTo("0-50")
        assertThat(dto.streamFrequency).isEqualTo("DAILY")
        assertThat(dto.status).isEqualTo("ACTIVE")
    }

    // endregion

    // region FilterCriteria.toStreamerMatchRequest()

    @Test
    fun `FilterCriteria toStreamerMatchRequest should wrap the mapped dto with the given page`() {
        // GIVEN
        val criteria = FilterCriteria(ageRange = 20..30)

        // WHEN
        val request = criteria.toStreamerMatchRequest(page = 3)

        // THEN
        assertThat(request.page).isEqualTo(3)
        assertThat(request.filter).isEqualTo(criteria.toDto())
    }

    // endregion

    // region TagDto <-> TagEntity

    @Test
    fun `TagDto toDomain should default a null category to an empty string`() {
        val dto = TagDto(id = 1, name = "FPS", category = null)
        assertThat(dto.toDomain()).isEqualTo(TagEntity(id = 1, name = "FPS", category = ""))
    }

    @Test
    fun `TagEntity toDto should convert a blank category to null`() {
        val entity = TagEntity(id = 1, name = "FPS", category = "")
        assertThat(entity.toDto().category).isNull()
    }

    @Test
    fun `TagEntity toDto should keep a non-blank category`() {
        val entity = TagEntity(id = 1, name = "FPS", category = "Gaming")
        assertThat(entity.toDto().category).isEqualTo("Gaming")
    }

    // endregion

    // region TagEntity <-> TagRoomModel

    @Test
    fun `TagEntity toRoomModel and TagRoomModel toDomain should round-trip`() {
        // GIVEN
        val entity = TagEntity(id = 1, name = "FPS", category = "Gaming")

        // WHEN
        val roundTripped = entity.toRoomModel().toDomain()

        // THEN
        assertThat(roundTripped).isEqualTo(entity)
    }

    // endregion

    // region FilterEntity <-> FilterRoomEntity

    @Test
    fun `FilterEntity toRoomEntity should flatten ageRange into minAge and maxAge`() {
        // GIVEN
        val entity = FilterEntity(
            id = "f1",
            name = "Chill",
            userId = "u1",
            criteria = FilterCriteria(ageRange = 22..45),
        )

        // WHEN
        val room = entity.toRoomEntity()

        // THEN
        assertThat(room.minAge).isEqualTo(22)
        assertThat(room.maxAge).isEqualTo(45)
    }

    @Test
    fun `FilterRoomEntity toDomainEntity should rebuild ageRange from minAge and maxAge`() {
        // GIVEN
        val room = FilterRoomEntity(
            id = "f1",
            name = "Chill",
            userId = "u1",
            minAge = 22,
            maxAge = 45,
            languages = emptyList(),
            platforms = emptyList(),
            personalities = emptyList(),
            tags = emptyList(),
            averageViewers = "",
            streamFrequency = "",
            status = "",
        )

        // WHEN
        val entity = room.toDomainEntity()

        // THEN
        assertThat(entity.criteria.ageRange).isEqualTo(22..45)
    }

    @Test
    fun `FilterEntity toRoomEntity then toDomainEntity should round-trip`() {
        // GIVEN
        val entity = FilterEntity(
            id = "f1",
            name = "Chill",
            userId = "u1",
            criteria = FilterCriteria(
                ageRange = 20..30,
                tags = listOf(TagEntity(id = 1, name = "FPS", category = "Gaming")),
                languages = listOf("FR"),
                platforms = listOf("Twitch"),
                personalities = listOf("Chill"),
                averageViewers = "0-50",
                streamFrequency = "DAILY",
                status = "ACTIVE",
            ),
        )

        // WHEN
        val roundTripped = entity.toRoomEntity().toDomainEntity()

        // THEN
        assertThat(roundTripped).isEqualTo(entity)
    }

    // endregion
}
