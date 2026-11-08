package com.strimup.feature.report.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VideoReportPolicyTest {

    @Test
    fun `a listed reason should be valid without details`() {
        assertThat(VideoReportPolicy.isValid(VideoReportReason.Violence, "")).isTrue()
    }

    @Test
    fun `the other reason should require details`() {
        assertThat(VideoReportPolicy.isValid(VideoReportReason.Other, "   ")).isFalse()
        assertThat(VideoReportPolicy.isValid(VideoReportReason.Other, "Vidéo trompeuse")).isTrue()
    }

    @Test
    fun `details longer than the limit should be invalid`() {
        val tooLong = "a".repeat(VideoReportPolicy.MAX_DETAILS_LENGTH + 1)
        val atLimit = "a".repeat(VideoReportPolicy.MAX_DETAILS_LENGTH)

        assertThat(VideoReportPolicy.isValid(VideoReportReason.Copyright, tooLong)).isFalse()
        assertThat(VideoReportPolicy.isValid(VideoReportReason.Copyright, atLimit)).isTrue()
    }

    @Test
    fun `the reasons should follow the display order`() {
        assertThat(VideoReportReason.entries.map { it.apiValue }).containsExactly(
            "SPAM_OR_SCAM",
            "HARASSMENT_OR_HATE",
            "VIOLENCE",
            "COPYRIGHT",
            "SEXUAL_CONTENT",
            "UNDERAGE",
            "OTHER",
        ).inOrder()
    }
}
