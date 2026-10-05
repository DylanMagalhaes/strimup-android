package com.strimup.feature.schedule.presentation.schedulesection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import com.strimup.feature.schedule.presentation.labelRes
import java.time.DayOfWeek
import java.time.LocalTime

private const val DEFAULT_START_HOUR = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddScheduleItemSheet(
    state: AddScheduleItemUiState,
    onDaySelected: (DayOfWeek) -> Unit,
    onStartTimeSelected: (LocalTime) -> Unit,
    onTitleChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier,
    ) {
        AddScheduleItemContent(
            state = state,
            onDaySelected = onDaySelected,
            onStartTimeSelected = onStartTimeSelected,
            onTitleChange = onTitleChange,
            onSubmitClick = onSubmitClick,
        )
    }
}

@Composable
private fun AddScheduleItemContent(
    state: AddScheduleItemUiState,
    onDaySelected: (DayOfWeek) -> Unit,
    onStartTimeSelected: (LocalTime) -> Unit,
    onTitleChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTimePickerVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.schedule_add_title),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.schedule_add_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        FormField(label = stringResource(R.string.schedule_add_day_label)) {
            DaySelector(
                selectedDay = state.selectedDay,
                isEnabled = !state.isSubmitting,
                onDaySelected = onDaySelected,
            )
        }

        FormField(label = stringResource(R.string.schedule_add_time_label)) {
            OutlinedButton(
                onClick = { isTimePickerVisible = true },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    text = state.startTime?.toDisplayTime()
                        ?: stringResource(R.string.schedule_add_time_placeholder),
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting,
            singleLine = true,
            label = { Text(text = stringResource(R.string.schedule_add_title_label)) },
            supportingText = {
                Text(
                    text = stringResource(
                        R.string.schedule_add_title_counter,
                        state.title.length,
                        SchedulePolicy.MAX_TITLE_LENGTH,
                    ),
                )
            },
        )

        state.errorMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        PrimaryButton(
            label = stringResource(R.string.schedule_add_submit),
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isSubmitEnabled,
            isLoading = state.isSubmitting,
        )
    }

    if (isTimePickerVisible) {
        StartTimePickerDialog(
            initialTime = state.startTime ?: LocalTime.of(DEFAULT_START_HOUR, 0),
            onConfirm = { startTime ->
                isTimePickerVisible = false
                onStartTimeSelected(startTime)
            },
            onDismiss = { isTimePickerVisible = false },
        )
    }
}

@Composable
private fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
private fun DaySelector(
    selectedDay: DayOfWeek?,
    isEnabled: Boolean,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DayOfWeek.entries.forEach { day ->
            FilterChip(
                selected = day == selectedDay,
                onClick = { onDaySelected(day) },
                enabled = isEnabled,
                label = { Text(text = stringResource(day.labelRes())) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartTimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute)) }) {
                Text(text = stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
        text = { TimePicker(state = timePickerState) },
    )
}

@Preview
@Composable
internal fun AddScheduleItemContentPreview() {
    StrimupTheme {
        Surface {
            AddScheduleItemContent(
                state = AddScheduleItemUiState(
                    selectedDay = DayOfWeek.WEDNESDAY,
                    startTime = LocalTime.parse("20:30"),
                    title = "GTA RP avec la commu",
                ),
                onDaySelected = {},
                onStartTimeSelected = {},
                onTitleChange = {},
                onSubmitClick = {},
            )
        }
    }
}
