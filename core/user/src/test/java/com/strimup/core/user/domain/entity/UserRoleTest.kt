package com.strimup.core.user.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserRoleTest {

    @Test
    fun `fromApi should map an exact uppercase match`() {
        assertThat(UserRole.fromApi("ADMIN")).isEqualTo(UserRole.ADMIN)
    }

    @Test
    fun `fromApi should map a lowercase value ignoring case`() {
        assertThat(UserRole.fromApi("streamer")).isEqualTo(UserRole.STREAMER)
    }

    @Test
    fun `fromApi should fallback to VIEWER for an unknown value`() {
        assertThat(UserRole.fromApi("MODERATOR")).isEqualTo(UserRole.VIEWER)
    }
}
