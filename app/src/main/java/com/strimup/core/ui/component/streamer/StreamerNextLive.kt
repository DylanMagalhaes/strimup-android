package com.strimup.core.ui.component.streamer

import com.strimup.core.streamer.domain.entity.Streamer
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DAYS_IN_WEEK = 7
private val displayTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

data class StreamerNextLive(
    val day: NextLiveDay,
    val startTime: String,
    val title: String,
)

sealed interface NextLiveDay {
    data object Today : NextLiveDay
    data object Tomorrow : NextLiveDay
    data class ThisWeek(val dayOfWeek: DayOfWeek) : NextLiveDay
    data class NextWeek(val dayOfWeek: DayOfWeek) : NextLiveDay
}

fun List<Streamer.ScheduleSlot>.nextLive(now: LocalDateTime): StreamerNextLive? =
    mapNotNull { slot -> slot.toUpcoming(now) }
        .minWithOrNull(compareBy<UpcomingSlot> { it.daysAhead }.thenBy { it.startTime })
        ?.toNextLive()

private data class UpcomingSlot(
    val daysAhead: Int,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val title: String,
)

private fun Streamer.ScheduleSlot.toUpcoming(now: LocalDateTime): UpcomingSlot? {
    val day = dayOfWeek.toDayOfWeekOrNull()
    val time = startTime.toLocalTimeOrNull()
    if (day == null || time == null) return null

    val daysAhead = (day.value - now.dayOfWeek.value + DAYS_IN_WEEK) % DAYS_IN_WEEK
    val isAlreadyPassed = daysAhead == 0 && time.isBefore(now.toLocalTime())

    return UpcomingSlot(
        daysAhead = if (isAlreadyPassed) DAYS_IN_WEEK else daysAhead,
        dayOfWeek = day,
        startTime = time,
        title = title,
    )
}

private fun UpcomingSlot.toNextLive(): StreamerNextLive = StreamerNextLive(
    day = when (daysAhead) {
        0 -> NextLiveDay.Today
        1 -> NextLiveDay.Tomorrow
        DAYS_IN_WEEK -> NextLiveDay.NextWeek(dayOfWeek)
        else -> NextLiveDay.ThisWeek(dayOfWeek)
    },
    startTime = startTime.format(displayTimeFormatter),
    title = title,
)

private fun Int.toDayOfWeekOrNull(): DayOfWeek? = try {
    DayOfWeek.of(this + 1)
} catch (_: DateTimeException) {
    null
}

private fun String.toLocalTimeOrNull(): LocalTime? = try {
    LocalTime.parse(this)
} catch (_: DateTimeException) {
    null
}
