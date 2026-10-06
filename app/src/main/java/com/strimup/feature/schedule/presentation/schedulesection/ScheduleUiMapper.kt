package com.strimup.feature.schedule.presentation.schedulesection

import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val displayTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

internal fun List<ScheduleItemEntity>.toScheduleDays(): List<ScheduleDayUi> =
    mapNotNull { item -> item.dayOfWeek.toDayOfWeekOrNull()?.let { day -> day to item.toSlotUi() } }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })
        .toSortedMap()
        .map { (day, slots) ->
            ScheduleDayUi(dayOfWeek = day, slots = slots.sortedBy { it.startTime })
        }

internal fun List<ScheduleDayUi>.withSlot(day: DayOfWeek, slot: ScheduleSlotUi): List<ScheduleDayUi> {
    val existingDay = firstOrNull { it.dayOfWeek == day }
    val updatedDay = existingDay?.copy(slots = (existingDay.slots + slot).sortedBy { it.startTime })
        ?: ScheduleDayUi(dayOfWeek = day, slots = listOf(slot))

    return (filterNot { it.dayOfWeek == day } + updatedDay).sortedBy { it.dayOfWeek }
}

internal fun List<ScheduleDayUi>.withoutSlot(itemId: String): List<ScheduleDayUi> =
    map { day -> day.copy(slots = day.slots.filterNot { it.id == itemId }) }
        .filter { it.slots.isNotEmpty() }

internal fun ScheduleItemEntity.toSlotUi(): ScheduleSlotUi = ScheduleSlotUi(
    id = id,
    startTime = startTime.toDisplayTime(),
    title = title,
)

internal fun DayOfWeek.toDayIndex(): Int = value - 1

internal fun LocalTime.toDisplayTime(): String = format(displayTimeFormatter)

private fun Int.toDayOfWeekOrNull(): DayOfWeek? = try {
    DayOfWeek.of(this + 1)
} catch (_: DateTimeException) {
    null
}

private fun String.toDisplayTime(): String = try {
    LocalTime.parse(this).toDisplayTime()
} catch (_: DateTimeException) {
    this
}
