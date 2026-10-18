package com.strimup.feature.auth.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.data.response.UserLoggedResponse
import org.junit.Test

class UserLoggedMapperTest {

    // UserLoggedResponse.toEntity()

    @Test
    fun `toEntity should correctly map full UserLoggedResponse to LoginResultEntity`() {
        // GIVEN
        val response = UserLoggedResponse(
            message = "Connexion réussie",
            token = "fake_jwt_token",
            refreshToken = "fake_refresh_token",
            userLogged = UserLoggedResponse.UserLogged(
                id = "1",
                email = "inox@test.com",
                userName = "Inox",
                role = "ADMIN",
                birthDate = "2000-01-01",
                gender = "MALE",
                avatarUrl = "https://example.com/avatar.png"
            )
        )

        // WHEN
        val result = response.toEntity()

        // THEN
        assertThat(result.message).isEqualTo("Connexion réussie")
        assertThat(result.token).isEqualTo("fake_jwt_token")
        assertThat(result.user.id).isEqualTo("1")
        assertThat(result.user.userName).isEqualTo("Inox")
        assertThat(result.user.email).isEqualTo("inox@test.com")
        assertThat(result.user.role).isEqualTo(UserRole.ADMIN)
        assertThat(result.user.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    @Test
    fun `toEntity should default message to empty string when message is null`() {
        // GIVEN
        val response = UserLoggedResponse(
            message = null,
            token = "fake_jwt_token",
            userLogged = UserLoggedResponse.UserLogged(
                id = "1",
                email = "inox@test.com",
                userName = "Inox",
                role = "STREAMER",
                avatarUrl = null
            )
        )

        // WHEN
        val result = response.toEntity()

        // THEN
        assertThat(result.message).isEmpty()
    }

    @Test
    fun `toEntity should handle lowercase role and convert to UserRole enum`() {
        // GIVEN
        val response = UserLoggedResponse(
            message = "OK",
            token = "token",
            userLogged = UserLoggedResponse.UserLogged(
                id = "1",
                email = "inox@test.com",
                userName = "Inox",
                role = "STREAMER",
                avatarUrl = null
            )
        )

        // WHEN
        val result = response.toEntity()

        // THEN
        assertThat(result.user.role).isEqualTo(UserRole.STREAMER)
    }

    @Test
    fun `toEntity should fallback to VIEWER when role is invalid`() {
        // GIVEN
        val response = UserLoggedResponse(
            message = "OK",
            token = "token",
            userLogged = UserLoggedResponse.UserLogged(
                id = "1",
                email = "inox@test.com",
                userName = "Inox",
                role = "INVALID_ROLE",
                avatarUrl = null
            )
        )

        // WHEN
        val result = response.toEntity()

        // THEN
        assertThat(result.user.role).isEqualTo(UserRole.VIEWER)
    }
}
