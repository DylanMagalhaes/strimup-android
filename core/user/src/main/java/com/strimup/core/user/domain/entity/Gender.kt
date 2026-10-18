package com.strimup.core.user.domain.entity

enum class Gender(val apiValue: String) {
    MALE("male"),
    FEMALE("female"),
    NON_BINARY("non_binary"),
    PREFER_NOT_TO_SAY("prefer_not_to_say")
}
