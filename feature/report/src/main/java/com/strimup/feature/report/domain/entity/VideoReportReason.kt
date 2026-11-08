package com.strimup.feature.report.domain.entity

enum class VideoReportReason(val apiValue: String) {
    SpamOrScam("SPAM_OR_SCAM"),
    HarassmentOrHate("HARASSMENT_OR_HATE"),
    Violence("VIOLENCE"),
    Copyright("COPYRIGHT"),
    SexualContent("SEXUAL_CONTENT"),
    Underage("UNDERAGE"),
    Other("OTHER"),
}
