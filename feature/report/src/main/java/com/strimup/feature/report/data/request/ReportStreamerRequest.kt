package com.strimup.feature.report.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportStreamerRequest(
    @SerialName("reason")
    val reason: String,
    @SerialName("details")
    val details: String? = null,
)
