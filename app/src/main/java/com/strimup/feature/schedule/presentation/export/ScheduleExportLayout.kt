package com.strimup.feature.schedule.presentation.export

import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.presentation.schedulesection.ScheduleSlotUi
import com.strimup.feature.schedule.presentation.schedulesection.toScheduleDays
import java.time.DayOfWeek

data class ScheduleExportDay(
    val dayOfWeek: DayOfWeek,
    val slots: List<ScheduleSlotUi>,
)

data class CellBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top
}

object ScheduleExportLayout {
    const val IMAGE_WIDTH = 1080
    const val IMAGE_HEIGHT = 1920
    const val CONTENT_HORIZONTAL_MARGIN = 84f
    const val CONTENT_TOP = 336f
    const val CONTENT_BOTTOM_MARGIN = 96f
    const val GRID_COLUMNS = 4
    const val GRID_ROWS = 2
    const val GRID_GAP = 30f

    const val CONTENT_LEFT = CONTENT_HORIZONTAL_MARGIN
    const val CONTENT_RIGHT = IMAGE_WIDTH - CONTENT_HORIZONTAL_MARGIN
    const val CONTENT_BOTTOM = IMAGE_HEIGHT - CONTENT_BOTTOM_MARGIN
    const val CONTENT_WIDTH = CONTENT_RIGHT - CONTENT_LEFT

    fun gridCells(gridTop: Float): List<CellBounds> {
        val cellWidth = (CONTENT_WIDTH - GRID_GAP * (GRID_COLUMNS - 1)) / GRID_COLUMNS
        val cellHeight = (CONTENT_BOTTOM - gridTop - GRID_GAP * (GRID_ROWS - 1)) / GRID_ROWS

        return (0 until GRID_ROWS).flatMap { row ->
            (0 until GRID_COLUMNS).map { column ->
                val left = CONTENT_LEFT + column * (cellWidth + GRID_GAP)
                val top = gridTop + row * (cellHeight + GRID_GAP)
                CellBounds(left = left, top = top, right = left + cellWidth, bottom = top + cellHeight)
            }
        }
    }
}

fun List<ScheduleItemEntity>.toExportDays(): List<ScheduleExportDay> {
    val scheduleDays = toScheduleDays()
    return DayOfWeek.entries.map { day ->
        ScheduleExportDay(
            dayOfWeek = day,
            slots = scheduleDays.firstOrNull { it.dayOfWeek == day }?.slots.orEmpty(),
        )
    }
}
