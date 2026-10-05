package com.strimup.feature.schedule.presentation.export

import com.strimup.core.ui.text.UiText
import java.io.File

data class ScheduleExportUiState(
    val selectedTemplate: ScheduleExportTemplate = ScheduleExportTemplate.Black,
    val imageFile: File? = null,
    val isGenerating: Boolean = true,
    val errorMessage: UiText? = null,
) {
    val isImageReady: Boolean
        get() = imageFile != null && !isGenerating && errorMessage == null
}
