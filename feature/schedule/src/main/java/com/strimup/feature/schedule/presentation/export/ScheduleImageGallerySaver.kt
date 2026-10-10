package com.strimup.feature.schedule.presentation.export

import java.io.File

interface ScheduleImageGallerySaver {
    val isAvailable: Boolean

    suspend fun save(file: File): Result<Unit>
}
