package com.strimup.core.user.data

import com.strimup.core.user.data.local.dao.UserDao
import com.strimup.core.user.data.mapper.toDomainEntity
import com.strimup.core.user.domain.UserRepository
import com.strimup.core.user.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {
    override fun getCurrentUser(): Flow<UserEntity?> {
        return userDao.getUserFlow().map { userRoomEntity ->
            userRoomEntity?.toDomainEntity()
        }
    }

    override suspend fun updateCurrentUserAvatar(avatarUrl: String) {
        userDao.updateAvatarUrl(avatarUrl)
    }
}
