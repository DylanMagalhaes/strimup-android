package com.strimup.feature.schedule.domain.entity

object SchedulePolicy {
    const val MAX_ITEMS = 14
    const val MIN_TITLE_LENGTH = 2
    const val MAX_TITLE_LENGTH = 60
    const val FIRST_DAY_OF_WEEK = 0
    const val LAST_DAY_OF_WEEK = 6

    fun isValidTitle(title: String): Boolean = title.trim().length in MIN_TITLE_LENGTH..MAX_TITLE_LENGTH

    fun isValid(item: NewScheduleItemEntity): Boolean =
        item.dayOfWeek in FIRST_DAY_OF_WEEK..LAST_DAY_OF_WEEK &&
            item.startTime.isNotBlank() &&
            isValidTitle(item.title)
}
