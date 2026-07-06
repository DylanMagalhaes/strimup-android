package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.local.dao.UserDao
import com.strimup.feature.auth.data.mapper.toDomainEntity
import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.data.mapper.toRoomEntity
import com.strimup.feature.auth.domain.UserRepository
import com.strimup.feature.auth.domain.entity.UserEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val service: AuthApiService
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
