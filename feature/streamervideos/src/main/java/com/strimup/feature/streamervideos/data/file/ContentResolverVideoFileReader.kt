package com.strimup.feature.streamervideos.data.file

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

class ContentResolverVideoFileReader @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : VideoFileReader {

    override suspend fun describe(uri: String): LocalVideoFile = withContext(Dispatchers.IO) {
        val videoUri = uri.toUri()
        var fileName: String? = null
        var sizeBytes: Long? = null

        context.contentResolver.query(
            videoUri,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0 && !cursor.isNull(nameIndex)) fileName = cursor.getString(nameIndex)
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) sizeBytes = cursor.getLong(sizeIndex)
            }
        }

        LocalVideoFile(
            uri = uri,
            fileName = fileName,
            mimeType = context.contentResolver.getType(videoUri),
            sizeBytes = sizeBytes,
        )
    }

    override fun open(uri: String): InputStream =
        context.contentResolver.openInputStream(uri.toUri()) ?: throw FileNotFoundException(uri)
}
