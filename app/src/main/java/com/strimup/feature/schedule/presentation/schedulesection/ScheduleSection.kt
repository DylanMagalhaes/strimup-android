package com.strimup.feature.schedule.presentation.schedulesection

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.ui.component.spacer.VerticalSpacer
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.schedule.presentation.labelRes
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun ScheduleSection(
    streamerId: String,
    modifier: Modifier = Modifier,
    viewModel: ScheduleSectionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val today = remember { LocalDate.now().dayOfWeek }

    LaunchedEffect(streamerId) {
        viewModel.loadSchedule(streamerId)
    }

    ScheduleSection(
        state = state,
        today = today,
        onRetryClick = { viewModel.loadSchedule(streamerId) },
        modifier = modifier,
    )
}

@Composable
private fun ScheduleSection(
    state: ScheduleSectionUiState,
    today: DayOfWeek,
    onRetryClick: () -> Unit,
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
                if (state.days.isEmpty()) {
                    ScheduleMessage(messageRes = R.string.schedule_empty)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.days.forEach { day ->
                            ScheduleDayCard(
                                day = day,
                                isToday = day.dayOfWeek == today,
                            )
                        }
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
                        Text(text = stringResource(R.string.action_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleDayCard(
    day: ScheduleDayUi,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (isToday) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(day.dayOfWeek.labelRes()),
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = zalandoFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                )
                if (isToday) {
                    TodayPill()
                }
            }

            day.slots.forEach { slot ->
                ScheduleSlotRow(slot = slot, isToday = isToday)
            }
        }
    }
}

@Composable
private fun ScheduleSlotRow(
    slot: ScheduleSlotUi,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = slot.startTime,
            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .widthIn(min = 56.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
        Text(
            text = slot.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TodayPill(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.schedule_today),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
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
                onRetryClick = {},
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
                onRetryClick = {},
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
                state = ScheduleSectionUiState.Error(messageRes = R.string.error_network),
                today = DayOfWeek.WEDNESDAY,
                onRetryClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
