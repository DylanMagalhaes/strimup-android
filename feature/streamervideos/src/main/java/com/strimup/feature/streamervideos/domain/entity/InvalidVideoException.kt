package com.strimup.feature.streamervideos.domain.entity

class InvalidVideoException(val reason: Reason) : IllegalArgumentException() {
    enum class Reason {
        UNSUPPORTED_FORMAT,
        FILE_TOO_LARGE,
        INVALID_TITLE,
        INVALID_DESCRIPTION,
    }
}
