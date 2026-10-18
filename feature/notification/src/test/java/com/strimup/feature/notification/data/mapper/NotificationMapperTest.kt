package com.strimup.feature.notification.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.notification.data.response.NotificationPageResponse
import com.strimup.feature.notification.data.response.NotificationResponse
import com.strimup.feature.notification.domain.entity.NotificationType
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.Instant

class NotificationMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun parse(body: String) = json.decodeFromString<NotificationResponse>(body).toDomain()

    @Test
    fun `new_favorite should expose the fan identity`() {
        val notification = parse(
            """
            {
              "id": "n1",
              "type": "new_favorite",
              "message": "Inox t'a ajouté en favori",
              "data": { "fan_pseudo": "Inox", "fan_id": "42" },
              "read_at": null,
              "created_at": "2026-09-29T10:15:30Z"
            }
            """
        )

        assertThat(notification.type).isEqualTo(NotificationType.NewFavorite(fanId = "42", fanPseudo = "Inox"))
        assertThat(notification.message).isEqualTo("Inox t'a ajouté en favori")
        assertThat(notification.isRead).isFalse()
        assertThat(notification.createdAt).isEqualTo(Instant.parse("2026-09-29T10:15:30Z"))
    }

    @Test
    fun `new_favorite with a numeric fan id should still be read`() {
        val notification = parse("""{ "id": "n1", "type": "new_favorite", "data": { "fan_id": 42 } }""")

        assertThat(notification.type).isEqualTo(NotificationType.NewFavorite(fanId = "42", fanPseudo = null))
    }

    @Test
    fun `global_announcement with null data should be parsed`() {
        val notification = parse(
            """
            {
              "id": "n2",
              "type": "global_announcement",
              "message": "Nouveauté",
              "data": null,
              "read_at": "2026-09-29T10:15:30Z",
              "created_at": "2026-09-29T10:00:00Z"
            }
            """
        )

        assertThat(notification.type).isEqualTo(NotificationType.GlobalAnnouncement)
        assertThat(notification.isRead).isTrue()
        assertThat(notification.isDeletable).isFalse()
    }

    @Test
    fun `ugc types should be recognised`() {
        assertThat(parse("""{ "id": "a", "type": "ugc_message" }""").type).isEqualTo(NotificationType.UgcMessage)
        assertThat(parse("""{ "id": "b", "type": "ugc_order_status" }""").type)
            .isEqualTo(NotificationType.UgcOrderStatus)
    }

    @Test
    fun `unknown type with free form data should not crash`() {
        val notification = parse(
            """
            {
              "id": "n3",
              "type": "brand_new_type",
              "message": "Hello",
              "data": { "nested": { "a": [1, 2] }, "flag": true }
            }
            """
        )

        assertThat(notification.type).isEqualTo(NotificationType.Unknown(rawType = "brand_new_type"))
        assertThat(notification.message).isEqualTo("Hello")
        assertThat(notification.isDeletable).isTrue()
    }

    @Test
    fun `data that is not an object should be ignored`() {
        val notification = parse("""{ "id": "n4", "type": "new_favorite", "data": ["unexpected"] }""")

        assertThat(notification.type).isEqualTo(NotificationType.NewFavorite(fanId = null, fanPseudo = null))
    }

    @Test
    fun `invalid created_at should give a null date instead of crashing`() {
        val notification = parse("""{ "id": "n5", "type": "ugc_message", "created_at": "hier" }""")

        assertThat(notification.createdAt).isNull()
    }

    @Test
    fun `page should keep total and hasMore`() {
        val page = json.decodeFromString<NotificationPageResponse>(
            """{ "items": [ { "id": "n1", "type": "ugc_message" } ], "total": 45, "hasMore": true }"""
        ).toDomain()

        assertThat(page.items).hasSize(1)
        assertThat(page.total).isEqualTo(45)
        assertThat(page.hasMore).isTrue()
    }
}
