package com.strimup.feature.schedule.presentation.schedulesection

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.ui.component.spacer.VerticalSpacer
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.schedule.R
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import java.time.DayOfWeek
import java.time.LocalDate
import com.strimup.core.ui.R as CoreUiR

@Composable
fun ScheduleSection(
    streamerId: String,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    snackBarHostState: SnackbarHostState? = null,
    onExportClick: (() -> Unit)? = null,
    viewModel: ScheduleSectionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val addItemState by viewModel.addItemState.collectAsStateWithLifecycle()
    var isAddSheetVisible by rememberSaveable { mutableStateOf(false) }
    val today = remember { LocalDate.now().dayOfWeek }
    val resources = LocalResources.current

    LaunchedEffect(streamerId) {
        viewModel.loadSchedule(streamerId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ScheduleSectionUiEvent.ShowSnackBar -> {
                    snackBarHostState?.showSnackbar(resources.getString(event.textRes))
                }

                ScheduleSectionUiEvent.ItemAdded -> {
                    isAddSheetVisible = false
                }
            }
        }
    }

    ScheduleSection(
        state = state,
        today = today,
        isEditable = isEditable,
        onRetryClick = { viewModel.loadSchedule(streamerId) },
        onDeleteClick = viewModel::onDeleteClick,
        onAddClick = { isAddSheetVisible = true },
        onExportClick = onExportClick,
        modifier = modifier,
    )

    if (isAddSheetVisible) {
        AddScheduleItemSheet(
            state = addItemState,
            onDaySelected = viewModel::onAddDaySelected,
            onStartTimeSelected = viewModel::onAddStartTimeSelected,
            onTitleChange = viewModel::onAddTitleChange,
            onSubmitClick = viewModel::onAddSubmitClick,
            onDismiss = {
                isAddSheetVisible = false
                viewModel.onAddDismiss()
            },
        )
    }
}

@Composable
private fun ScheduleSection(
    state: ScheduleSectionUiState,
    today: DayOfWeek,
    isEditable: Boolean,
    onRetryClick: () -> Unit,
    onDeleteClick: (itemId: String) -> Unit,
    onAddClick: () -> Unit,
    onExportClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.schedule_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )

        VerticalSpacer(8.dp)

        when (state) {
            is ScheduleSectionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }

            is ScheduleSectionUiState.Success -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.days.isEmpty()) {
                        ScheduleMessage(messageRes = R.string.schedule_empty)
                    }

                    state.days.forEach { day ->
                        ScheduleDayCard(
                            day = day,
                            isToday = day.dayOfWeek == today,
                            isEditable = isEditable,
                            deletingItemIds = state.deletingItemIds,
                            onDeleteClick = onDeleteClick,
                        )
                    }

                    if (isEditable) {
                        AddScheduleItemButton(
                            canAddItem = state.canAddItem,
                            itemCount = state.itemCount,
                            onClick = onAddClick,
                        )
                    }

                    if (isEditable && onExportClick != null) {
                        ExportScheduleButton(
                            isEnabled = state.itemCount > 0,
                            onClick = onExportClick,
                        )
                    }
                }
            }

            is ScheduleSectionUiState.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScheduleMessage(
                        messageRes = state.messageRes,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onRetryClick) {
                        Text(text = stringResource(CoreUiR.string.action_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddScheduleItemButton(
    canAddItem: Boolean,
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (canAddItem) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = stringResource(R.string.schedule_add_item),
                fontWeight = FontWeight.Bold,
            )
        }
    } else {
        Text(
            text = stringResource(R.string.schedule_full, itemCount, SchedulePolicy.MAX_ITEMS),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier,
        )
    }
}

@Composable
private fun ExportScheduleButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Outlined.IosShare,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = stringResource(R.string.schedule_export_action),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ScheduleMessage(
    @StringRes messageRes: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(messageRes),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}

private val previewDays = listOf(
    ScheduleDayUi(
        dayOfWeek = DayOfWeek.MONDAY,
        slots = listOf(
            ScheduleSlotUi(id = "1", startTime = "14:00", title = "Just Chatting & Q/R"),
            ScheduleSlotUi(id = "2", startTime = "21:00", title = "Soirée Valorant ranked"),
        ),
    ),
    ScheduleDayUi(
        dayOfWeek = DayOfWeek.WEDNESDAY,
        slots = listOf(
            ScheduleSlotUi(id = "3", startTime = "10:00", title = "Café et revue de la semaine"),
            ScheduleSlotUi(id = "4", startTime = "18:30", title = "GTA RP avec la commu"),
            ScheduleSlotUi(id = "5", startTime = "22:00", title = "Horror games jusqu'au bout de la nuit"),
        ),
    ),
    ScheduleDayUi(
        dayOfWeek = DayOfWeek.SATURDAY,
        slots = listOf(
            ScheduleSlotUi(id = "6", startTime = "20:00", title = "Tournoi communautaire"),
        ),
    ),
)

@Preview
@Composable
internal fun ScheduleSectionPreview() {
    StrimupTheme {
        Surface {
            ScheduleSection(
                state = ScheduleSectionUiState.Success(days = previewDays),
                today = DayOfWeek.WEDNESDAY,
                isEditable = false,
                onRetryClick = {},
                onDeleteClick = {},
                onAddClick = {},
                onExportClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
internal fun ScheduleSectionEmptyPreview() {
    StrimupTheme {
        Surface {
            ScheduleSection(
                state = ScheduleSectionUiState.Success(days = emptyList()),
                today = DayOfWeek.WEDNESDAY,
                isEditable = true,
                onRetryClick = {},
                onDeleteClick = {},
                onAddClick = {},
                onExportClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
internal fun ScheduleSectionErrorPreview() {
    StrimupTheme {
        Surface {
            ScheduleSection(
                state = ScheduleSectionUiState.Error(messageRes = CoreUiR.string.error_network),
                today = DayOfWeek.WEDNESDAY,
                isEditable = false,
                onRetryClick = {},
                onDeleteClick = {},
                onAddClick = {},
                onExportClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
internal fun ScheduleSectionEditablePreview() {
    StrimupTheme {
        Surface {
            ScheduleSection(
                state = ScheduleSectionUiState.Success(days = previewDays, deletingItemIds = setOf("4")),
                today = DayOfWeek.WEDNESDAY,
                isEditable = true,
                onRetryClick = {},
                onDeleteClick = {},
                onAddClick = {},
                onExportClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
