package com.strimup.feature.cuts.domain.entity

class ExpiredCutsCursorException : Exception()

class CutUnavailableException : Exception()

class LikeRateLimitedException(val serverMessage: String?) : Exception()
