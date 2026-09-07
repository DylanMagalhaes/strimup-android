package com.strimup.core.user.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.data.local.model.UserRoomEntity
import com.strimup.core.user.data.response.UserMeResponse
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import org.junit.Test

class UserMeMapperTest {

    // UserMeData.toEntity()

    @Test
    fun `toEntity should correctly map UserMeData to UserEntity`() {
        // GIVEN
        val userMeData = UserMeResponse.UserMeData(
            id = "1",
            email = "test@strimup.com",
            userName = "Inox",
            role = "STREAMER",
            birthDate = "2000-01-01",
            gender = "MALE",
            isTwitchConnected = true,
            imageUrl = "https://example.com/avatar.png"
        )

        // WHEN
        val result = userMeData.toEntity()

        // THEN
        assertThat(result.id).isEqualTo("1")
        assertThat(result.userName).isEqualTo("Inox")
        assertThat(result.email).isEqualTo("test@strimup.com")
        assertThat(result.role).isEqualTo(UserRole.STREAMER)
        assertThat(result.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toEntity should throw IllegalArgumentException when role is invalid`() {
        // GIVEN
        val userMeData = UserMeResponse.UserMeData(
            id = "1",
            email = "test@strimup.com",
            userName = "Inox",
            role = "INVALID_ROLE",
            birthDate = "2000-01-01",
            gender = "MALE",
            isTwitchConnected = false,
            imageUrl = "https://example.com/avatar.png"
        )

        // WHEN
        userMeData.toEntity()

        // THEN -> Exception
    }

    // UserEntity.toRoomEntity()

    @Test
    fun `toRoomEntity should correctly map UserEntity to UserRoomEntity`() {
        // GIVEN
        val userEntity = UserEntity(
            id = "1",
            userName = "Inox",
            email = "test@strimup.com",
            role = UserRole.STREAMER,
            avatarUrl = "https://example.com/avatar.png"
        )

        // WHEN
        val result = userEntity.toRoomEntity()

        // THEN
        assertThat(result.id).isEqualTo("1")
        assertThat(result.userName).isEqualTo("Inox")
        assertThat(result.email).isEqualTo("test@strimup.com")
        assertThat(result.role).isEqualTo("STREAMER")
        assertThat(result.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    // UserRoomEntity.toDomainEntity()

    @Test
    fun `toDomainEntity should correctly map UserRoomEntity to UserEntity`() {
        // GIVEN
        val roomEntity = UserRoomEntity(
            id = "1",
            userName = "Inox",
            email = "test@strimup.com",
            role = "STREAMER",
            avatarUrl = "https://example.com/avatar.png"
        )

        // WHEN
        val result = roomEntity.toDomainEntity()

        // THEN
        assertThat(result.id).isEqualTo("1")
        assertThat(result.userName).isEqualTo("Inox")
        assertThat(result.email).isEqualTo("test@strimup.com")
        assertThat(result.role).isEqualTo(UserRole.STREAMER)
        assertThat(result.avatarUrl).isEqualTo("https://example.com/avatar.png")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toDomainEntity should throw IllegalArgumentException when stored role is invalid`() {
        // GIVEN
        val roomEntity = UserRoomEntity(
            id = "1",
            userName = "Inox",
            email = "test@strimup.com",
            role = "UNKNOWN_ROLE",
            avatarUrl = null
        )

        // WHEN
        roomEntity.toDomainEntity()

        // THEN -> Exception
    }
}