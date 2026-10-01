package com.strimup.feature.push.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.feature.notification.domain.entity.NotificationType
import org.junit.Test

class PushTitlesTest {

    @Test
    fun `new favorite should have its own default title`() {
        assertThat(NotificationType.NewFavorite(fanId = null, fanPseudo = null).defaultPushTitleRes())
            .isEqualTo(R.string.push_title_new_favorite)
    }

    @Test
    fun `other types should fall back to the app name`() {
        assertThat(NotificationType.GlobalAnnouncement.defaultPushTitleRes()).isEqualTo(R.string.push_title_default)
        assertThat(NotificationType.Unknown("x").defaultPushTitleRes()).isEqualTo(R.string.push_title_default)
    }
}
