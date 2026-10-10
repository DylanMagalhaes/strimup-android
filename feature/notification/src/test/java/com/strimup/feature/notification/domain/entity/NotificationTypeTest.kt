package com.strimup.feature.notification.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotificationTypeTest {

    @Test
    fun `fromApi should map every known type`() {
        assertThat(NotificationType.fromApi("new_favorite", fanId = "42", fanPseudo = "Inox"))
            .isEqualTo(NotificationType.NewFavorite(fanId = "42", fanPseudo = "Inox"))
        assertThat(NotificationType.fromApi("global_announcement")).isEqualTo(NotificationType.GlobalAnnouncement)
        assertThat(NotificationType.fromApi("ugc_message")).isEqualTo(NotificationType.UgcMessage)
        assertThat(NotificationType.fromApi("ugc_order_status")).isEqualTo(NotificationType.UgcOrderStatus)
    }

    @Test
    fun `fromApi should keep unknown types`() {
        assertThat(NotificationType.fromApi("something_new")).isEqualTo(NotificationType.Unknown("something_new"))
        assertThat(NotificationType.fromApi("")).isEqualTo(NotificationType.Unknown(""))
    }
}
