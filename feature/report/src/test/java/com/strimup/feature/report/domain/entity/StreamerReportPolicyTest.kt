package com.strimup.feature.report.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StreamerReportPolicyTest {

    @Test
    fun `a listed reason should be valid without details`() {
        assertThat(StreamerReportPolicy.isValid(ReportReason.SpamOrScam, "")).isTrue()
    }

    @Test
    fun `the other reason should require details`() {
        assertThat(StreamerReportPolicy.isValid(ReportReason.Other, "   ")).isFalse()
        assertThat(StreamerReportPolicy.isValid(ReportReason.Other, "Faux compte")).isTrue()
    }

    @Test
    fun `details longer than the limit should be invalid`() {
        val tooLong = "a".repeat(StreamerReportPolicy.MAX_DETAILS_LENGTH + 1)
        val atLimit = "a".repeat(StreamerReportPolicy.MAX_DETAILS_LENGTH)

        assertThat(StreamerReportPolicy.isValid(ReportReason.Impersonation, tooLong)).isFalse()
        assertThat(StreamerReportPolicy.isValid(ReportReason.Impersonation, atLimit)).isTrue()
    }

    @Test
    fun `surrounding spaces should not count in the limit`() {
        val details = "  " + "a".repeat(StreamerReportPolicy.MAX_DETAILS_LENGTH) + "  "

        assertThat(StreamerReportPolicy.isValid(ReportReason.Impersonation, details)).isTrue()
    }
}
