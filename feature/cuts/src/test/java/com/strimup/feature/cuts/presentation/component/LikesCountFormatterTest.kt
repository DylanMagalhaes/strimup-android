package com.strimup.feature.cuts.presentation.component

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LikesCountFormatterTest {

    @Test
    fun `small counts should be shown as is`() {
        assertThat(formatLikesCount(0)).isEqualTo("0")
        assertThat(formatLikesCount(999)).isEqualTo("999")
    }

    @Test
    fun `thousands should use a short format with a comma`() {
        assertThat(formatLikesCount(1_000)).isEqualTo("1k")
        assertThat(formatLikesCount(1_250)).isEqualTo("1,2k")
        assertThat(formatLikesCount(12_345)).isEqualTo("12,3k")
        assertThat(formatLikesCount(999_999)).isEqualTo("999,9k")
    }

    @Test
    fun `millions should use the M suffix`() {
        assertThat(formatLikesCount(1_200_000)).isEqualTo("1,2M")
    }
}
