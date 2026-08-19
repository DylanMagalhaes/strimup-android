package com.strimup.core.streamer.domain.repository

import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions

interface StreamerRepository {
    suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>>

    suspend fun getLiveStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>>

    suspend fun getFavoriteStreamerIds(): List<String>

    suspend fun searchStreamers(userName: String): Result<List<Streamer>>

    suspend fun getStreamerById(id: String): Result<Streamer>

    suspend fun updateProfile(streamer: Streamer): Result<Streamer>

    suspend fun updateAvatar(uri: String): Result<String>

    suspend fun getStreamerOptions(): Result<StreamerOptions>

    suspend fun getStreamersByFilter(request: StreamerMatchRequest): Result<StreamerMatchResult>
}
