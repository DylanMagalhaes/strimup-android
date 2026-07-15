package com.strimup.common.user.data

import com.strimup.common.user.data.local.dao.UserDao
import com.strimup.common.user.data.mapper.toDomainEntity
import com.strimup.common.user.data.mapper.toEntity
import com.strimup.common.user.data.mapper.toRoomEntity
import com.strimup.common.user.domain.UserRepository
import com.strimup.common.user.domain.entity.UserEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val service: UserApiService
) : UserRepository {
    override fun getCurrentUser(): Flow<UserEntity?> {
        return userDao.getUserFlow().map { userRoomEntity ->
            userRoomEntity?.toDomainEntity()
        }
    }

    override suspend fun refreshCurrentUser(): Result<UserEntity> {
        return runCatching {
            val response = service.getCurrentUser()

            val userEntity = response.user.toEntity()

            userDao.insertUser(userEntity.toRoomEntity())

            userEntity
        }
    }
}