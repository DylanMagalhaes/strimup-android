package com.strimup.feature.notification.presentation.time

import com.google.common.truth.Truth.assertThat
import com.strimup.core.ui.text.UiText
import com.strimup.feature.notification.R
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class RelativeTimeTest {

    private val now = Instant.parse("2026-09-29T12:00:00Z")

    private fun ago(duration: Duration) = now.minus(duration).toRelativeTimeUiText(now = now, zoneId = ZoneOffset.UTC)

    @Test
    fun `less than a minute should be just now`() {
        assertThat(ago(Duration.ofSeconds(30))).isEqualTo(UiText.Resource(R.string.time_just_now))
    }

    @Test
    fun `a date slightly in the future should be just now`() {
        assertThat(ago(Duration.ofSeconds(-20))).isEqualTo(UiText.Resource(R.string.time_just_now))
    }

    @Test
    fun `minutes should be shown under an hour`() {
        assertThat(ago(Duration.ofMinutes(5))).isEqualTo(UiText.Resource(R.string.time_minutes_ago, listOf(5)))
        assertThat(ago(Duration.ofMinutes(59))).isEqualTo(UiText.Resource(R.string.time_minutes_ago, listOf(59)))
    }

    @Test
    fun `hours should be shown under a day`() {
        assertThat(ago(Duration.ofHours(3))).isEqualTo(UiText.Resource(R.string.time_hours_ago, listOf(3)))
    }

    @Test
    fun `between one and two days should be yesterday`() {
        assertThat(ago(Duration.ofHours(30))).isEqualTo(UiText.Resource(R.string.time_yesterday))
    }

    @Test
    fun `days should be shown under a week`() {
        assertThat(ago(Duration.ofDays(4))).isEqualTo(UiText.Resource(R.string.time_days_ago, listOf(4)))
    }

    @Test
    fun `older dates of the same year should show day and month`() {
        assertThat(ago(Duration.ofDays(17))).isEqualTo(UiText.Dynamic("12 sept."))
    }

    @Test
    fun `dates of a previous year should include the year`() {
        assertThat(ago(Duration.ofDays(365))).isEqualTo(UiText.Dynamic("29 sept. 2025"))
    }
}
