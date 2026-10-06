package com.strimup.core.ui.component.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YouTubePlayerStateTest {

    private val commands = FakeYouTubePlayerCommands()
    private val state = YouTubePlayerState(commands)

    @Test
    fun `toggle should play when the video is paused`() {
        state.onPlaybackStateChanged(YouTubePlaybackState.PAUSED)

        state.togglePlayPause()

        assertThat(commands.playCount).isEqualTo(1)
        assertThat(commands.pauseCount).isEqualTo(0)
    }

    @Test
    fun `toggle should pause when the video is playing`() {
        state.onPlaybackStateChanged(YouTubePlaybackState.PLAYING)

        state.togglePlayPause()

        assertThat(commands.pauseCount).isEqualTo(1)
        assertThat(commands.playCount).isEqualTo(0)
    }

    @Test
    fun `buffering should be considered as playing`() {
        state.onPlaybackStateChanged(YouTubePlaybackState.BUFFERING)

        assertThat(state.isPlaying).isTrue()
    }

    @Test
    fun `progress should update current time and duration`() {
        state.onProgress(currentTime = 42f, duration = 120f)

        assertThat(state.currentTime).isEqualTo(42f)
        assertThat(state.duration).isEqualTo(120f)
    }

    @Test
    fun `seek by should move relative to the current time`() {
        state.onProgress(currentTime = 30f, duration = 120f)

        state.seekBy(10f)

        assertThat(commands.seeks).containsExactly(40f)
        assertThat(state.currentTime).isEqualTo(40f)
    }

    @Test
    fun `seek should not go before the start`() {
        state.onProgress(currentTime = 5f, duration = 120f)

        state.seekBy(-10f)

        assertThat(commands.seeks).containsExactly(0f)
    }

    @Test
    fun `seek should not go past the end`() {
        state.onProgress(currentTime = 115f, duration = 120f)

        state.seekBy(10f)

        assertThat(commands.seeks).containsExactly(120f)
    }
}
