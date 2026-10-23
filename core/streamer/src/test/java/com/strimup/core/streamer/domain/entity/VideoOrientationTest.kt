package com.strimup.core.streamer.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VideoOrientationTest {

    @Test
    fun `a video taller than wide is vertical`() {
        assertThat(VideoOrientation.fromDimensions(width = 1080, height = 1920)).isEqualTo(VideoOrientation.VERTICAL)
    }

    @Test
    fun `a video wider than tall is horizontal`() {
        assertThat(VideoOrientation.fromDimensions(width = 1920, height = 1080)).isEqualTo(VideoOrientation.HORIZONTAL)
    }

    @Test
    fun `a square video is horizontal`() {
        assertThat(VideoOrientation.fromDimensions(width = 720, height = 720)).isEqualTo(VideoOrientation.HORIZONTAL)
    }

    @Test
    fun `a landscape frame rotated a quarter turn is vertical`() {
        val orientation = VideoOrientation.fromDimensions(width = 1920, height = 1080, rotationDegrees = 90)

        assertThat(orientation).isEqualTo(VideoOrientation.VERTICAL)
    }

    @Test
    fun `a portrait frame rotated three quarter turns is horizontal`() {
        val orientation = VideoOrientation.fromDimensions(width = 1080, height = 1920, rotationDegrees = 270)

        assertThat(orientation).isEqualTo(VideoOrientation.HORIZONTAL)
    }

    @Test
    fun `a portrait frame rotated a half turn stays vertical`() {
        val orientation = VideoOrientation.fromDimensions(width = 1080, height = 1920, rotationDegrees = 180)

        assertThat(orientation).isEqualTo(VideoOrientation.VERTICAL)
    }

    @Test
    fun `unknown dimensions give no orientation`() {
        assertThat(VideoOrientation.fromDimensions(width = 0, height = 1920)).isNull()
    }
}
