package com.strimup.feature.report.presentation

import androidx.annotation.StringRes
import com.strimup.feature.report.R
import com.strimup.feature.report.domain.entity.VideoReportReason

@StringRes
fun VideoReportReason.labelRes(): Int = when (this) {
    VideoReportReason.SpamOrScam -> R.string.report_video_reason_spam_or_scam
    VideoReportReason.HarassmentOrHate -> R.string.report_video_reason_harassment_or_hate
    VideoReportReason.Violence -> R.string.report_video_reason_violence
    VideoReportReason.Copyright -> R.string.report_video_reason_copyright
    VideoReportReason.SexualContent -> R.string.report_video_reason_sexual_content
    VideoReportReason.Underage -> R.string.report_video_reason_underage
    VideoReportReason.Other -> R.string.report_video_reason_other
}
