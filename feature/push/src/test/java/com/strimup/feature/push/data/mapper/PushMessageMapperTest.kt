package com.strimup.feature.push.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.notification.domain.entity.NotificationType
import com.strimup.feature.push.domain.entity.PushMessage
import org.junit.Test

class PushMessageMapperTest {

    @Test
    fun `complete payload should be mapped`() {
        val message = mapOf(
            "notification_id" to "n1",
            "type" to "new_favorite",
            "title" to "Nouveau fan",
            "body" to "Inox t'a ajouté en favori",
            "created_at" to "2026-10-01T10:00:00Z",
        ).toPushMessage()

        assertThat(message).isEqualTo(
            PushMessage(
                notificationId = "n1",
                type = NotificationType.NewFavorite(fanId = null, fanPseudo = null),
                title = "Nouveau fan",
                body = "Inox t'a ajouté en favori",
            )
        )
    }

    @Test
    fun `payload without body should be ignored`() {
        assertThat(mapOf("type" to "new_favorite", "title" to "Nouveau fan").toPushMessage()).isNull()
        assertThat(mapOf("body" to "   ").toPushMessage()).isNull()
    }

    @Test
    fun `unknown or missing type should still give a message`() {
        assertThat(mapOf("type" to "brand_new", "body" to "Hello").toPushMessage()?.type)
            .isEqualTo(NotificationType.Unknown("brand_new"))
        assertThat(mapOf("body" to "Hello").toPushMessage()?.type).isEqualTo(NotificationType.Unknown(""))
    }

    @Test
    fun `blank optional fields should be null`() {
        val message = mapOf("notification_id" to "", "title" to " ", "body" to "Hello").toPushMessage()

        assertThat(message?.notificationId).isNull()
        assertThat(message?.title).isNull()
    }
}
