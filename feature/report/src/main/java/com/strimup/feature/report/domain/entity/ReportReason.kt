package com.strimup.feature.report.domain.entity

enum class ReportReason(val apiValue: String) {
    SexualContent("SEXUAL_CONTENT"),
    HarassmentOrHate("HARASSMENT_OR_HATE"),
    Impersonation("IMPERSONATION"),
    SpamOrScam("SPAM_OR_SCAM"),
    Underage("UNDERAGE"),
    Other("OTHER"),
}
