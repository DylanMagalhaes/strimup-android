package com.strimup.feature.schedule.presentation.export

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ScheduleExportFileNameTest {

    @Test
    fun `file name should be planning followed by the username`() {
        assertThat(scheduleExportFileName("raziuko")).isEqualTo("planning-raziuko.png")
    }

    @Test
    fun `unsafe characters should be replaced by dashes`() {
        assertThat(scheduleExportFileName("  Zé Gamer/42 ")).isEqualTo("planning-Z-Gamer-42.png")
    }

    @Test
    fun `a username without safe characters should fall back to strimup`() {
        assertThat(scheduleExportFileName("✨✨")).isEqualTo("planning-strimup.png")
    }
}
