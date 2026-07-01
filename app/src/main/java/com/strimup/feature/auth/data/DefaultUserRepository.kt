package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.domain.UserRepository
import com.strimup.feature.auth.domain.entity.UserEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultUserRepository @Inject constructor(
    private val service: AuthApiService
) : UserRepository {
    override fun getCurrentUser(): Flow<UserEntity> {
        TODO()
    }

    override suspend fun refreshCurrentUser(): Result<UserEntity> {
        return runCatching {
            val response = service.getCurrentUser()

            val userentity = response.user.toEntity()

            userentity


            // userDao.insertUser(userEntity)

            // userEntity

        }
    }

}