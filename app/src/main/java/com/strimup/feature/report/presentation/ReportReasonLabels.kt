package com.strimup.feature.report.presentation

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.feature.report.domain.entity.ReportReason

@StringRes
fun ReportReason.labelRes(): Int = when (this) {
    ReportReason.SexualContent -> R.string.report_reason_sexual_content
    ReportReason.HarassmentOrHate -> R.string.report_reason_harassment_or_hate
    ReportReason.Impersonation -> R.string.report_reason_impersonation
    ReportReason.SpamOrScam -> R.string.report_reason_spam_or_scam
    ReportReason.Underage -> R.string.report_reason_underage
    ReportReason.Other -> R.string.report_reason_other
}
