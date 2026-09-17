package com.strimup.feature.auth.data

import com.strimup.core.favorite.data.local.dao.FavoriteDao
import com.strimup.core.network.toDomainResult
import com.strimup.core.user.data.local.dao.UserDao
import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.data.mapper.toRoomEntity
import com.strimup.feature.auth.data.request.LoginRequest
import com.strimup.feature.auth.data.request.RegisterRequest
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import com.strimup.feature.filter.data.local.dao.FilterDao
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val service: AuthApiService,
    private val preferences: AuthPreferencesDataSource,
    private val userDao: UserDao,
    private val filterDao: FilterDao,
    private val favoriteDao: FavoriteDao
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<LoginResultEntity> {
        return runCatching {
            val response = service.login(LoginRequest(email, password))

            preferences.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken ?: ""
            )

            val loginResult = response.toEntity()

            userDao.insertUser(loginResult.user.toRoomEntity())

            loginResult
        }
    }

    override suspend fun register(credentials: RegisterCredentials): Result<LoginResultEntity> {
        return runCatching {
            val request = RegisterRequest(
                role = credentials.role.apiValue,
                userName = credentials.userName,
                password = credentials.password,
                gender = credentials.gender.apiValue,
                email = credentials.email,
                birthDate = credentials.birthDate,
            )
            val response = service.register(request)

            preferences.saveTokens(
                accessToken = response.token,
                refreshToken = ""
            )

            val registerResult = response.toEntity()

            userDao.insertUser(registerResult.user.toRoomEntity())

            registerResult
        }.toDomainResult()
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            preferences.clear()
            userDao.deleteAllUsers()
            filterDao.deleteAllFilters()
            favoriteDao.deleteAllFavorites()
        }.toDomainResult()
    }
}