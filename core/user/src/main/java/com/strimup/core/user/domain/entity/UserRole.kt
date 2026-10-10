package com.strimup.core.user.domain.entity

enum class UserRole(val apiValue: String) {
    ADMIN("admin"),
    STREAMER("streamer"),
    VIEWER("viewer");

    companion object {
        fun fromApi(role: String): UserRole =
            entries.firstOrNull { it.apiValue == role.lowercase() } ?: VIEWER
    }
}
