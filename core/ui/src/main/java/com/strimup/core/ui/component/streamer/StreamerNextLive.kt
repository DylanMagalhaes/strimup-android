package com.strimup.core.ui.component.streamer

import com.strimup.core.streamer.domain.entity.Streamer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val displayTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

data class StreamerNextLive(
    val day: NextLiveDay,
    val startTime: String,
    val title: String,
)

sealed interface NextLiveDay {
    data object Today : NextLiveDay
    data object Tomorrow : NextLiveDay
    data class Later(val dayOfWeek: DayOfWeek) : NextLiveDay
}

fun Streamer.NextLive.toDisplay(today: LocalDate, zone: ZoneId): StreamerNextLive? {
    val start = startsAt.atZone(zone)
    val date = start.toLocalDate()
    if (date.isBefore(today)) return null

    return StreamerNextLive(
        day = when (date) {
            today -> NextLiveDay.Today
            today.plusDays(1) -> NextLiveDay.Tomorrow
            else -> NextLiveDay.Later(date.dayOfWeek)
        },
        startTime = start.format(displayTimeFormatter),
        title = title,
    )
}
