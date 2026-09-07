package com.strimup.feature.auth.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.data.local.model.UserRoomEntity
import com.strimup.core.user.domain.entity.UserEntity
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

    @Test(expected = IllegalArgumentException::class)
    fun `toEntity should throw IllegalArgumentException when role is invalid`() {
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
        response.toEntity()

        // THEN -> Exception
    }

    // UserEntity.toRoomEntity()

    @Test
    fun `toRoomEntity should correctly map UserEntity to UserRoomEntity`() {
        // GIVEN
        val userEntity = UserEntity(
            id = "1",
            userName = "Inox",
            email = "inox@test.com",
            role = UserRole.STREAMER,
            avatarUrl = "https://example.com/avatar.png"
        )

        // WHEN
        val roomEntity = userEntity.toRoomEntity()

        // THEN
        assertThat(roomEntity.id).isEqualTo("1")
        assertThat(roomEntity.userName).isEqualTo("Inox")
        assertThat(roomEntity.email).isEqualTo("inox@test.com")
        assertThat(roomEntity.role).isEqualTo("STREAMER")
        assertThat(roomEntity.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    // UserRoomEntity.toDomainEntity()

    @Test
    fun `toDomainEntity should correctly map UserRoomEntity to UserEntity`() {
        // GIVEN
        val roomEntity = UserRoomEntity(
            id = "1",
            userName = "Inox",
            email = "inox@test.com",
            role = "STREAMER",
            avatarUrl = "https://example.com/avatar.png"
        )

        // WHEN
        val userEntity = roomEntity.toDomainEntity()

        // THEN
        assertThat(userEntity.id).isEqualTo("1")
        assertThat(userEntity.userName).isEqualTo("Inox")
        assertThat(userEntity.email).isEqualTo("inox@test.com")
        assertThat(userEntity.role).isEqualTo(UserRole.STREAMER)
        assertThat(userEntity.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toDomainEntity should throw IllegalArgumentException when stored role is invalid`() {
        // GIVEN
        val roomEntity = UserRoomEntity(
            id = "1",
            userName = "Inox",
            email = "inox@test.com",
            role = "UNKNOWN_ROLE",
            avatarUrl = null
        )

        // WHEN
        roomEntity.toDomainEntity()

        // THEN -> Exception
    }
}