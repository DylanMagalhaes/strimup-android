package com.strimup.core.streamer.data.avatar

class AvatarFile(
    val bytes: ByteArray,
    val mimeType: String,
)

interface AvatarFileReader {
    suspend fun read(uri: String): AvatarFile
}
