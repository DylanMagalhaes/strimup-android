package com.strimup.core.ui.component.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.R

private const val TODAY_PILL_ALPHA = 0.15f

@Composable
fun TodayPill(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.schedule_today),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = TODAY_PILL_ALPHA))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
fun ScheduleTimePill(
    time: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time,
        color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .widthIn(min = 56.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
