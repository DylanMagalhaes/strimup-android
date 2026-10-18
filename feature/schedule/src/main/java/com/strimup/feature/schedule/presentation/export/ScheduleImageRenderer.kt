package com.strimup.feature.schedule.presentation.export

import android.graphics.Bitmap

fun interface ScheduleImageRenderer {
    suspend fun render(
        username: String,
        days: List<ScheduleExportDay>,
        template: ScheduleExportTemplate,
    ): Bitmap
}
