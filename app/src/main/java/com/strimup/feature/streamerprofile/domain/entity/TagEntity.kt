package com.strimup.feature.streamerprofile.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class TagEntity(
    val id: Int,
    val name: String,
    val category: String
)