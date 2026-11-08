package com.strimup.feature.report.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.ui.component.button.DangerButton
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.BrandNeonGreen
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.report.R
import com.strimup.feature.report.domain.entity.VideoReportPolicy
import com.strimup.feature.report.domain.entity.VideoReportReason

@Composable
fun ReportVideoSheet(
    videoId: String,
    onDismiss: () -> Unit,
    onVideoUnavailable: () -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportVideoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnVideoUnavailable by rememberUpdatedState(onVideoUnavailable)
    val currentOnLoginRequired by rememberUpdatedState(onLoginRequired)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ReportVideoUiEvent.VideoUnavailable -> currentOnVideoUnavailable()
                ReportVideoUiEvent.LoginRequired -> currentOnLoginRequired()
            }
        }
    }

    val dismiss = {
        viewModel.onDismiss()
        onDismiss()
    }

    ReportVideoSheet(
        state = state,
        onReasonSelected = viewModel::onReasonSelected,
        onDetailsChange = viewModel::onDetailsChange,
        onSubmitClick = { viewModel.onSubmitClick(videoId) },
        onDismiss = dismiss,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportVideoSheet(
    state: ReportVideoUiState,
    onReasonSelected: (VideoReportReason) -> Unit,
    onDetailsChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier,
    ) {
        val confirmationRes = state.confirmationRes
        if (confirmationRes != null) {
            ReportVideoConfirmation(messageRes = confirmationRes, onCloseClick = onDismiss)
        } else {
            ReportVideoForm(
                state = state,
                onReasonSelected = onReasonSelected,
                onDetailsChange = onDetailsChange,
                onSubmitClick = onSubmitClick,
            )
        }
    }
}

@Composable
private fun ReportVideoForm(
    state: ReportVideoUiState,
    onReasonSelected: (VideoReportReason) -> Unit,
    onDetailsChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.report_video_title),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.report_video_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(modifier = Modifier.selectableGroup()) {
            VideoReportReason.entries.forEach { reason ->
                ReportVideoReasonRow(
                    label = stringResource(reason.labelRes()),
                    isSelected = reason == state.selectedReason,
                    isEnabled = !state.isSubmitting,
                    onClick = { onReasonSelected(reason) },
                )
            }
        }

        ReportVideoDetailsField(state = state, onDetailsChange = onDetailsChange)

        state.errorMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        DangerButton(
            label = stringResource(R.string.report_submit),
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isSubmitEnabled,
            isLoading = state.isSubmitting,
        )

        Text(
            text = stringResource(R.string.report_video_anonymity),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ReportVideoDetailsField(
    state: ReportVideoUiState,
    onDetailsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = state.details,
        onValueChange = onDetailsChange,
        modifier = modifier.fillMaxWidth(),
        enabled = !state.isSubmitting,
        isError = state.isDetailsTooLong,
        label = {
            val labelRes = if (state.isDetailsRequired) {
                R.string.report_details_label_required
            } else {
                R.string.report_details_label_optional
            }
            Text(text = stringResource(labelRes))
        },
        supportingText = {
            Text(
                text = stringResource(
                    R.string.report_details_counter,
                    state.details.trim().length,
                    VideoReportPolicy.MAX_DETAILS_LENGTH,
                ),
            )
        },
        minLines = 3,
        maxLines = 5,
    )
}

@Composable
private fun ReportVideoReasonRow(
    label: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
                selected = isSelected,
                enabled = isEnabled,
                role = Role.RadioButton,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(selected = isSelected, onClick = null, enabled = isEnabled)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ReportVideoConfirmation(
    messageRes: Int,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = BrandNeonGreen,
            modifier = Modifier.size(56.dp),
        )
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.report_video_confirmation),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        PrimaryButton(
            label = stringResource(R.string.report_video_close),
            modifier = Modifier.fillMaxWidth(),
            onClick = onCloseClick,
        )
    }
}

@Preview
@Composable
internal fun ReportVideoFormPreview() {
    StrimupTheme {
        Surface {
            ReportVideoForm(
                state = ReportVideoUiState(selectedReason = VideoReportReason.Other, details = "Vidéo trompeuse"),
                onReasonSelected = {},
                onDetailsChange = {},
                onSubmitClick = {},
            )
        }
    }
}

@Preview
@Composable
internal fun ReportVideoConfirmationPreview() {
    StrimupTheme {
        Surface {
            ReportVideoConfirmation(messageRes = R.string.report_video_sent, onCloseClick = {})
        }
    }
}
