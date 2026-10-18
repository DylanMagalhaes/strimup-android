package com.strimup.core.streamer.data.avatar

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AvatarImageSizingTest {

    @Test
    fun `small images should not be sampled`() {
        assertThat(avatarSampleSize(width = 800, height = 600)).isEqualTo(1)
        assertThat(avatarSampleSize(width = 1024, height = 1024)).isEqualTo(1)
    }

    @Test
    fun `big camera photos should be sampled without going under the target`() {
        assertThat(avatarSampleSize(width = 4000, height = 3000)).isEqualTo(2)
        assertThat(avatarSampleSize(width = 9248, height = 6936)).isEqualTo(8)
    }

    @Test
    fun `sampling should rely on the longest side for portrait photos`() {
        assertThat(avatarSampleSize(width = 3000, height = 4000)).isEqualTo(2)
    }

    @Test
    fun `small images should keep their size`() {
        assertThat(avatarTargetSize(width = 640, height = 480)).isEqualTo(640 to 480)
    }

    @Test
    fun `big images should be scaled down keeping their ratio`() {
        assertThat(avatarTargetSize(width = 2000, height = 1500)).isEqualTo(1024 to 768)
        assertThat(avatarTargetSize(width = 1500, height = 2000)).isEqualTo(768 to 1024)
    }

    @Test
    fun `extreme ratios should never produce an empty side`() {
        assertThat(avatarTargetSize(width = 10000, height = 2).second).isEqualTo(1)
    }
}
