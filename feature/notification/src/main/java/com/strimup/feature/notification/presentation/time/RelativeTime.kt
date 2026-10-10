package com.strimup.feature.notification.presentation.time

import com.strimup.core.ui.text.UiText
import com.strimup.feature.notification.R
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ONE_MINUTE: Duration = Duration.ofMinutes(1)
private val ONE_HOUR: Duration = Duration.ofHours(1)
private val ONE_DAY: Duration = Duration.ofDays(1)
private val TWO_DAYS: Duration = Duration.ofDays(2)
private const val DAYS_IN_WEEK = 7L
private val ONE_WEEK: Duration = Duration.ofDays(DAYS_IN_WEEK)
private val DATE_LOCALE: Locale = Locale.FRENCH

fun Instant.toRelativeTimeUiText(
    now: Instant,
    zoneId: ZoneId = ZoneId.systemDefault(),
): UiText {
    val elapsed = Duration.between(this, now)

    return when {
        elapsed < ONE_MINUTE -> UiText.Resource(R.string.time_just_now)
        elapsed < ONE_HOUR -> UiText.Resource(R.string.time_minutes_ago, listOf(elapsed.toMinutes().toInt()))
        elapsed < ONE_DAY -> UiText.Resource(R.string.time_hours_ago, listOf(elapsed.toHours().toInt()))
        elapsed < TWO_DAYS -> UiText.Resource(R.string.time_yesterday)
        elapsed < ONE_WEEK -> UiText.Resource(R.string.time_days_ago, listOf(elapsed.toDays().toInt()))
        else -> UiText.Dynamic(formatDate(now = now, zoneId = zoneId))
    }
}

private fun Instant.formatDate(now: Instant, zoneId: ZoneId): String {
    val date = atZone(zoneId)
    val isSameYear = date.year == now.atZone(zoneId).year
    val pattern = if (isSameYear) "d MMM" else "d MMM yyyy"
    return DateTimeFormatter.ofPattern(pattern, DATE_LOCALE).format(date)
}
