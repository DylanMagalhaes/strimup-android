package com.strimup.feature.schedule.presentation.schedulesection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.core.ui.component.schedule.ScheduleTimePill
import com.strimup.core.ui.component.schedule.TodayPill
import com.strimup.core.ui.schedule.labelRes
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
internal fun ScheduleDayCard(
    day: ScheduleDayUi,
    isToday: Boolean,
    isEditable: Boolean,
    deletingItemIds: Set<String>,
    onDeleteClick: (itemId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (isToday) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        },
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
                ScheduleSlotRow(
                    slot = slot,
                    isToday = isToday,
                    isEditable = isEditable,
                    isDeleting = slot.id in deletingItemIds,
                    onDeleteClick = { onDeleteClick(slot.id) },
                )
            }
        }
    }
}

@Composable
private fun ScheduleSlotRow(
    slot: ScheduleSlotUi,
    isToday: Boolean,
    isEditable: Boolean,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScheduleTimePill(time = slot.startTime, isHighlighted = isToday)
        Text(
            text = slot.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (isEditable) {
            DeleteSlotButton(
                title = slot.title,
                isDeleting = isDeleting,
                onClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun DeleteSlotButton(
    title: String,
    isDeleting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isDeleting) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
            )
        } else {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.schedule_delete_item, title),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}
