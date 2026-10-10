package com.strimup.core.ui.component.streamer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.R
import com.strimup.core.ui.component.schedule.ScheduleTimePill
import com.strimup.core.ui.component.schedule.TodayPill
import com.strimup.core.ui.schedule.shortLabelRes
import com.strimup.core.ui.theme.zalandoFontFamily

private const val BORDER_ALPHA = 0.4f

@Composable
fun StreamerNextLiveCard(
    nextLive: StreamerNextLive,
    modifier: Modifier = Modifier,
) {
    val isToday = nextLive.day == NextLiveDay.Today

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (isToday) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = BORDER_ALPHA))
        },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.streamer_next_live),
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = zalandoFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                if (isToday) {
                    TodayPill()
                } else {
                    Text(
                        text = nextLive.day.label(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ScheduleTimePill(time = nextLive.startTime, isHighlighted = isToday)
                Text(
                    text = nextLive.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NextLiveDay.label(): String = when (this) {
    NextLiveDay.Today -> stringResource(R.string.schedule_today)
    NextLiveDay.Tomorrow -> stringResource(R.string.streamer_next_live_tomorrow)
    is NextLiveDay.Later -> stringResource(dayOfWeek.shortLabelRes())
}
