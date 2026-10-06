package com.strimup.feature.schedule.presentation.export

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DefaultScheduleImageGallerySaver @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ScheduleImageGallerySaver {

    override val isAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override suspend fun save(file: File): Result<Unit> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return Result.failure(UnsupportedOperationException())
        }
        return runCatching { withContext(Dispatchers.IO) { insertIntoGallery(file) } }
            .onFailure { if (it is CancellationException) throw it }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun insertIntoGallery(file: File) {
        val resolver = context.contentResolver
        val pendingValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, PNG_MIME_TYPE)
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$ALBUM_NAME")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = checkNotNull(resolver.insert(collection, pendingValues)) { "MediaStore insert failed" }

        runCatching {
            val outputStream = checkNotNull(resolver.openOutputStream(uri)) { "Cannot open $uri" }
            outputStream.use { output -> file.inputStream().use { input -> input.copyTo(output) } }
            resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
        }.onFailure {
            resolver.delete(uri, null, null)
        }.getOrThrow()
    }

    private companion object {
        const val PNG_MIME_TYPE = "image/png"
        const val ALBUM_NAME = "StrimUp"
    }
}
