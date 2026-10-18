package com.strimup.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackTimeFormatterTest {

    @Test
    fun `short durations should be formatted as minutes and seconds`() {
        assertThat(formatPlaybackTime(0f)).isEqualTo("0:00")
        assertThat(formatPlaybackTime(65.7f)).isEqualTo("1:05")
    }

    @Test
    fun `long durations should include hours`() {
        assertThat(formatPlaybackTime(3_725f)).isEqualTo("1:02:05")
    }

    @Test
    fun `negative values should be formatted as zero`() {
        assertThat(formatPlaybackTime(-3f)).isEqualTo("0:00")
    }
}
