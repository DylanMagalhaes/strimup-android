package com.strimup.feature.report.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.core.ui.component.button.DangerButton
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.StreamerReportPolicy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportStreamerSheet(
    streamerName: String,
    state: ReportStreamerUiState,
    onReasonSelected: (ReportReason) -> Unit,
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
        ReportStreamerContent(
            streamerName = streamerName,
            state = state,
            onReasonSelected = onReasonSelected,
            onDetailsChange = onDetailsChange,
            onSubmitClick = onSubmitClick,
        )
    }
}

@Composable
private fun ReportStreamerContent(
    streamerName: String,
    state: ReportStreamerUiState,
    onReasonSelected: (ReportReason) -> Unit,
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
                text = stringResource(R.string.report_title, streamerName),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.report_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(modifier = Modifier.selectableGroup()) {
            ReportReason.entries.forEach { reason ->
                ReportReasonRow(
                    label = stringResource(reason.labelRes()),
                    isSelected = reason == state.selectedReason,
                    isEnabled = !state.isSubmitting,
                    onClick = { onReasonSelected(reason) },
                )
            }
        }

        OutlinedTextField(
            value = state.details,
            onValueChange = onDetailsChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting,
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
                        state.details.length,
                        StreamerReportPolicy.MAX_DETAILS_LENGTH,
                    ),
                )
            },
            minLines = 3,
            maxLines = 5,
        )

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
    }
}

@Composable
private fun ReportReasonRow(
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

@Preview
@Composable
internal fun ReportStreamerContentPreview() {
    StrimupTheme {
        Surface {
            ReportStreamerContent(
                streamerName = "Squeezie",
                state = ReportStreamerUiState(selectedReason = ReportReason.Other, details = "Faux compte"),
                onReasonSelected = {},
                onDetailsChange = {},
                onSubmitClick = {},
            )
        }
    }
}
