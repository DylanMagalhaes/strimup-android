package com.strimup.feature.schedule.presentation.export

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.strimup.feature.schedule.R

enum class ScheduleExportTemplate(
    @param:DrawableRes val backgroundRes: Int,
    @param:StringRes val labelRes: Int,
) {
    Black(backgroundRes = R.drawable.schedule_template_black, labelRes = R.string.schedule_export_template_black),
    Pink(backgroundRes = R.drawable.schedule_template_pink, labelRes = R.string.schedule_export_template_pink),
}
