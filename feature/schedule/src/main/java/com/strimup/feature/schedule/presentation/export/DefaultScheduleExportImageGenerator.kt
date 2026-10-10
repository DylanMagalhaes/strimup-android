package com.strimup.feature.schedule.presentation.export

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DefaultScheduleExportImageGenerator @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val renderer: ScheduleImageRenderer,
) : ScheduleExportImageGenerator {

    override suspend fun generate(
        username: String,
        days: List<ScheduleExportDay>,
        template: ScheduleExportTemplate,
    ): Result<File> = runCatching {
        val bitmap = renderer.render(username = username, days = days, template = template)
        withContext(Dispatchers.IO) {
            val directory = File(context.cacheDir, "$EXPORT_DIRECTORY/${template.name.lowercase()}").apply { mkdirs() }
            val file = File(directory, scheduleExportFileName(username))
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, it) }
            bitmap.recycle()
            file
        }
    }.onFailure { if (it is CancellationException) throw it }

    private companion object {
        const val EXPORT_DIRECTORY = "schedule_export"
        const val PNG_QUALITY = 100
    }
}
