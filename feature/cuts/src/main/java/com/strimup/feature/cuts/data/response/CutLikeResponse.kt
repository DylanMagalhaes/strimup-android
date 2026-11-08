package com.strimup.feature.cuts.data.response

import kotlinx.serialization.Serializable

@Serializable
data class CutLikeResponse(
    val liked: Boolean,
    val likesCount: Int,
)
