package com.strimup.feature.notification.presentation.bell

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UnreadBadgeLabelTest {

    @Test
    fun `badge should be hidden when there is nothing unread`() {
        assertThat(unreadBadgeLabel(0)).isNull()
    }

    @Test
    fun `badge should be hidden for an unexpected negative count`() {
        assertThat(unreadBadgeLabel(-1)).isNull()
    }

    @Test
    fun `badge should show the exact count up to 9`() {
        assertThat(unreadBadgeLabel(3)).isEqualTo("3")
        assertThat(unreadBadgeLabel(9)).isEqualTo("9")
    }

    @Test
    fun `badge should show 9+ above 9`() {
        assertThat(unreadBadgeLabel(10)).isEqualTo("9+")
        assertThat(unreadBadgeLabel(12)).isEqualTo("9+")
    }
}
