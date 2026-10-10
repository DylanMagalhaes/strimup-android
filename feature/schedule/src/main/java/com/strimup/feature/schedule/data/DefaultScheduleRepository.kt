package com.strimup.feature.schedule.data

import com.strimup.core.network.toDomainResult
import com.strimup.feature.schedule.data.mapper.toCreateScheduleItemRequest
import com.strimup.feature.schedule.data.mapper.toEntity
import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import javax.inject.Inject

class DefaultScheduleRepository @Inject constructor(
    private val service: ScheduleApiService
) : ScheduleRepository {
    override suspend fun getSchedule(streamerId: String): Result<List<ScheduleItemEntity>> {
        return runCatching {
            service.getSchedule(streamerId).map {
                it.toEntity()
            }
        }.toDomainResult()
    }

    override suspend fun getMySchedule(): Result<List<ScheduleItemEntity>> {
        return runCatching {
            service.getMySchedule().map { it.toEntity() }
        }.toDomainResult()
    }

    override suspend fun createScheduleItem(item: NewScheduleItemEntity): Result<ScheduleItemEntity> {
        return runCatching {
            service.createScheduleItem(item.toCreateScheduleItemRequest()).toEntity()
        }.toDomainResult()
    }

    override suspend fun deleteScheduleItem(id: String): Result<Unit> {
        return runCatching {
            service.deleteScheduleItem(id)
        }.toDomainResult()
    }
}
