package com.strimup.feature.cuts.domain.entity

data class CutsPage(
    val cuts: List<Cut>,
    val nextCursor: String?,
)
