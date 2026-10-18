package com.strimup.feature.streamervideos.data.file

import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import java.io.InputStream

interface VideoFileReader {
    suspend fun describe(uri: String): LocalVideoFile

    fun open(uri: String): InputStream
}
