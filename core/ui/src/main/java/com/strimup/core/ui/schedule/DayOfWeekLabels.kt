package com.strimup.core.ui.schedule

import androidx.annotation.StringRes
import com.strimup.core.ui.R
import java.time.DayOfWeek

@StringRes
fun DayOfWeek.labelRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.schedule_day_monday
    DayOfWeek.TUESDAY -> R.string.schedule_day_tuesday
    DayOfWeek.WEDNESDAY -> R.string.schedule_day_wednesday
    DayOfWeek.THURSDAY -> R.string.schedule_day_thursday
    DayOfWeek.FRIDAY -> R.string.schedule_day_friday
    DayOfWeek.SATURDAY -> R.string.schedule_day_saturday
    DayOfWeek.SUNDAY -> R.string.schedule_day_sunday
}

@StringRes
fun DayOfWeek.exportLabelRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.schedule_export_day_monday
    DayOfWeek.TUESDAY -> R.string.schedule_export_day_tuesday
    DayOfWeek.WEDNESDAY -> R.string.schedule_export_day_wednesday
    DayOfWeek.THURSDAY -> R.string.schedule_export_day_thursday
    DayOfWeek.FRIDAY -> R.string.schedule_export_day_friday
    DayOfWeek.SATURDAY -> R.string.schedule_export_day_saturday
    DayOfWeek.SUNDAY -> R.string.schedule_export_day_sunday
}

@StringRes
fun DayOfWeek.shortLabelRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.day_short_monday
    DayOfWeek.TUESDAY -> R.string.day_short_tuesday
    DayOfWeek.WEDNESDAY -> R.string.day_short_wednesday
    DayOfWeek.THURSDAY -> R.string.day_short_thursday
    DayOfWeek.FRIDAY -> R.string.day_short_friday
    DayOfWeek.SATURDAY -> R.string.day_short_saturday
    DayOfWeek.SUNDAY -> R.string.day_short_sunday
}
