package com.strimup.feature.schedule.domain.entity

object SchedulePolicy {
    const val MAX_ITEMS = 14
    const val MAX_TITLE_LENGTH = 50
    const val FIRST_DAY_OF_WEEK = 0
    const val LAST_DAY_OF_WEEK = 6

    fun isValid(item: NewScheduleItemEntity): Boolean {
        val trimmedTitle = item.title.trim()
        return item.dayOfWeek in FIRST_DAY_OF_WEEK..LAST_DAY_OF_WEEK &&
            item.startTime.isNotBlank() &&
            trimmedTitle.isNotEmpty() &&
            trimmedTitle.length <= MAX_TITLE_LENGTH
    }
}
