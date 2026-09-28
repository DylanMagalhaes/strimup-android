package com.strimup.feature.auth.data

import com.strimup.BuildConfig
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.network.toDomainResult
import com.strimup.core.user.data.local.dao.UserDao
import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import com.strimup.feature.auth.data.local.LocalSessionDataSource
import com.strimup.feature.auth.data.local.OAuthVerifierDataSource
import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.data.mapper.toRoomEntity
import com.strimup.feature.auth.data.pkce.Pkce
import com.strimup.feature.auth.data.request.LoginRequest
import com.strimup.feature.auth.data.request.LogoutRequest
import com.strimup.feature.auth.data.request.OAuthCompleteRequest
import com.strimup.feature.auth.data.request.OAuthExchangeRequest
import com.strimup.feature.auth.data.request.RegisterRequest
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCredentials
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val service: AuthApiService,
    private val preferences: AuthPreferencesDataSource,
    private val oauthVerifier: OAuthVerifierDataSource,
    private val userDao: UserDao,
    private val localSession: LocalSessionDataSource,
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
        }.toDomainResult()
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
                acceptedTerms = credentials.hasAcceptedTerms,
            )
            val response = service.register(request)

            preferences.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken
            )

            val registerResult = response.toEntity()

            userDao.insertUser(registerResult.user.toRoomEntity())

            registerResult
        }.toDomainResult()
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            revokeRefreshToken()
            localSession.clear()
        }.toDomainResult()
    }

    private suspend fun revokeRefreshToken() {
        val refreshToken = preferences.getRefreshToken()
        if (refreshToken.isNullOrBlank()) return

        try {
            service.logout(LogoutRequest(refreshToken))
        } catch (_: IOException) {
        } catch (_: HttpException) {
        }
    }

    override suspend fun createTwitchLoginUrl(): Result<String> {
        return runCatching {
            val verifier = Pkce.generateVerifier()
            oauthVerifier.save(verifier)

            "${BuildConfig.BASE_URL}api/auth/twitch/login" +
                "?client=android" +
                "&code_challenge=${Pkce.challengeOf(verifier)}" +
                "&code_challenge_method=S256"
        }.toDomainResult()
    }

    override suspend fun exchangeOAuthCode(code: String): Result<LoginResultEntity> {
        return runCatching {
            val verifier = oauthVerifier.get() ?: throw DomainException(DomainError.Unknown)

            val response = try {
                service.exchangeOAuthCode(OAuthExchangeRequest(code = code, codeVerifier = verifier))
            } finally {
                oauthVerifier.clear()
            }

            preferences.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken
            )

            val loginResult = response.toEntity()

            userDao.insertUser(loginResult.user.toRoomEntity())

            loginResult
        }.toDomainResult()
    }

    override suspend fun completeOAuth(credentials: OAuthCredentials): Result<LoginResultEntity> {
        return runCatching {
            val verifier = oauthVerifier.get() ?: throw DomainException(DomainError.Unknown)

            val request = OAuthCompleteRequest(
                tmp = credentials.tmp,
                codeVerifier = verifier,
                role = credentials.role.apiValue,
                birthDate = credentials.birthDate,
                gender = credentials.gender.apiValue,
                acceptedTerms = credentials.hasAcceptedTerms,
            )
            val response = service.completeOAuth(request)
            oauthVerifier.clear()

            preferences.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken
            )

            val oauthResult = response.toEntity()

            userDao.insertUser(oauthResult.user.toRoomEntity())

            oauthResult
        }.toDomainResult()
    }
}
