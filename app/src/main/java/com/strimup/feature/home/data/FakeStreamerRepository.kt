package com.strimup.feature.home.data

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class FakeStreamerRepository @Inject constructor() : StreamerRepository {

    override suspend fun getStreamers(
        filter: FilterEntity,
        favoriteStreamerIds: List<String>
    ): Result<List<StreamerEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteStreamerIds(): List<String> {
        throw NotImplementedError()
    }
}