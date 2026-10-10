package com.strimup.feature.schedule.presentation.export

import java.io.File

fun interface ScheduleExportImageGenerator {
    suspend fun generate(
        username: String,
        days: List<ScheduleExportDay>,
        template: ScheduleExportTemplate,
    ): Result<File>
}
