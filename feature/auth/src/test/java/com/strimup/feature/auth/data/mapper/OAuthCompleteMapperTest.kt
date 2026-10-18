package com.strimup.feature.auth.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.data.response.OAuthCompleteResponse
import com.strimup.feature.auth.data.response.UserRegisteredResponse
import org.junit.Test

class OAuthCompleteMapperTest {

    @Test
    fun `toEntity should correctly map full OAuthCompleteResponse to LoginResultEntity`() {
        // GIVEN
        val response = OAuthCompleteResponse(
            message = "Connexion réussie",
            token = "fake_jwt_token",
            isNewUser = true,
            user = UserRegisteredResponse.UserRegistered(
                id = "1",
                email = "inox@test.com",
                userName = "Inox",
                role = "viewer",
                birthDate = "1995-05-05",
                gender = "male",
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
        assertThat(result.user.role).isEqualTo(UserRole.VIEWER)
        assertThat(result.user.avatarUrl).isNull()
    }

    @Test
    fun `toEntity should fallback to VIEWER when role is invalid`() {
        // GIVEN
        val response = OAuthCompleteResponse(
            message = "Connexion réussie",
            token = "token",
            user = UserRegisteredResponse.UserRegistered(
                id = "1",
                email = "inox@test.com",
                userName = "Inox",
                role = "INVALID_ROLE",
            )
        )

        // WHEN
        val result = response.toEntity()

        // THEN
        assertThat(result.user.role).isEqualTo(UserRole.VIEWER)
    }
}
