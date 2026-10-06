package com.strimup.core.ui.component.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YouTubePlaybackStateTest {

    @Test
    fun `known codes should map to their playback state`() {
        assertThat(YouTubePlaybackState.fromCode(1)).isEqualTo(YouTubePlaybackState.PLAYING)
        assertThat(YouTubePlaybackState.fromCode(2)).isEqualTo(YouTubePlaybackState.PAUSED)
        assertThat(YouTubePlaybackState.fromCode(0)).isEqualTo(YouTubePlaybackState.ENDED)
    }

    @Test
    fun `unknown code should fall back to unstarted`() {
        assertThat(YouTubePlaybackState.fromCode(42)).isEqualTo(YouTubePlaybackState.UNSTARTED)
    }
}
