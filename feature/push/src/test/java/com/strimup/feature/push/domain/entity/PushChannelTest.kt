package com.strimup.feature.push.domain.entity

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.notification.domain.entity.NotificationType
import org.junit.Test

class PushChannelTest {

    @Test
    fun `new favorite should use the favorites channel`() {
        assertThat(PushChannel.from(NotificationType.NewFavorite(fanId = "1", fanPseudo = "Inox")))
            .isEqualTo(PushChannel.FAVORITES)
    }

    @Test
    fun `global announcement should use the announcements channel`() {
        assertThat(PushChannel.from(NotificationType.GlobalAnnouncement)).isEqualTo(PushChannel.ANNOUNCEMENTS)
    }

    @Test
    fun `other types should use the general channel`() {
        assertThat(PushChannel.from(NotificationType.UgcMessage)).isEqualTo(PushChannel.GENERAL)
        assertThat(PushChannel.from(NotificationType.UgcOrderStatus)).isEqualTo(PushChannel.GENERAL)
        assertThat(PushChannel.from(NotificationType.Unknown("brand_new"))).isEqualTo(PushChannel.GENERAL)
    }

    @Test
    fun `channel ids should stay stable`() {
        assertThat(PushChannel.entries.map { it.id }).containsExactly("favorites", "announcements", "general")
    }
}
