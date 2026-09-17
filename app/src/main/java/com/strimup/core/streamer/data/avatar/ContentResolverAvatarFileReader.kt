package com.strimup.core.streamer.data.avatar

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val DEFAULT_MIME_TYPE = "image/jpeg"

class ContentResolverAvatarFileReader @Inject constructor(
    @ApplicationContext private val context: Context
) : AvatarFileReader {

    override fun read(uri: String): AvatarFile {
        val parsedUri = Uri.parse(uri)
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(parsedUri) ?: DEFAULT_MIME_TYPE

        val bytes = contentResolver.openInputStream(parsedUri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("Impossible de lire l'image")

        return AvatarFile(bytes = bytes, mimeType = mimeType)
    }
}
