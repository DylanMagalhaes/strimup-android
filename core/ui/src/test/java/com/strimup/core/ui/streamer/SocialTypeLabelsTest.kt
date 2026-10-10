package com.strimup.core.ui.streamer

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Social
import org.junit.Test

class SocialTypeLabelsTest {

    @Test
    fun `every social network should use its official brand name`() {
        assertThat(Social.Type.entries.map { it.displayName() })
            .containsExactly("Twitch", "Kick", "YouTube", "Instagram", "TikTok")
            .inOrder()
    }
}
