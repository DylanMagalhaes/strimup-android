package com.strimup.core.user.domain.entity

enum class UserRole {
    ADMIN,
    STREAMER,
    VIEWER;

    companion object {
        fun fromApi(role: String): UserRole =
            entries.firstOrNull { it.name == role.uppercase() } ?: VIEWER
    }
}
