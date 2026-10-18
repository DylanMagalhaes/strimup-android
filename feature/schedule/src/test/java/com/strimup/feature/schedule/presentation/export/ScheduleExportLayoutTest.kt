package com.strimup.feature.schedule.presentation.export

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import org.junit.Test
import java.time.DayOfWeek

class ScheduleExportLayoutTest {

    @Test
    fun `gridCells should return 4 columns and 2 rows filling the content area with 30px gaps`() {
        val gridTop = 600f

        val cells = ScheduleExportLayout.gridCells(gridTop)

        assertThat(cells).hasSize(8)
        assertThat(cells.first().left).isEqualTo(84f)
        assertThat(cells.first().top).isEqualTo(gridTop)
        assertThat(cells[3].right).isWithin(0.01f).of(996f)
        assertThat(cells.last().bottom).isWithin(0.01f).of(1824f)
        assertThat(cells[1].left - cells[0].right).isWithin(0.01f).of(30f)
        assertThat(cells[4].top - cells[0].bottom).isWithin(0.01f).of(30f)
        assertThat(cells.map { it.width }.distinct()).hasSize(1)
    }

    @Test
    fun `toExportDays should return the 7 days starting on monday with empty days kept`() {
        val items = listOf(
            ScheduleItemEntity(id = "1", title = "Valorant", dayOfWeek = 6, startTime = "21:00"),
            ScheduleItemEntity(id = "2", title = "GTA RP", dayOfWeek = 0, startTime = "20:00"),
        )

        val days = items.toExportDays()

        assertThat(days.map { it.dayOfWeek }).containsExactlyElementsIn(DayOfWeek.entries).inOrder()
        assertThat(days.first().slots.map { it.title }).containsExactly("GTA RP")
        assertThat(days[1].slots).isEmpty()
        assertThat(days.last().slots.map { it.title }).containsExactly("Valorant")
    }

    @Test
    fun `toExportDays should sort the slots of a day by start time`() {
        val items = listOf(
            ScheduleItemEntity(id = "1", title = "Soir", dayOfWeek = 2, startTime = "21:00"),
            ScheduleItemEntity(id = "2", title = "Matin", dayOfWeek = 2, startTime = "08:00"),
            ScheduleItemEntity(id = "3", title = "Aprem", dayOfWeek = 2, startTime = "15:30"),
        )

        val wednesday = items.toExportDays()[2]

        assertThat(wednesday.slots.map { it.startTime }).containsExactly("08:00", "15:30", "21:00").inOrder()
    }
}
