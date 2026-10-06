package com.strimup.core.ui.component.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.strimup.R
import com.strimup.core.util.formatPlaybackTime

@Composable
fun PlayerProgressBar(
    currentTime: Float,
    duration: Float,
    onSeek: (Float) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragPosition by remember { mutableStateOf<Float?>(null) }
    val position = dragPosition ?: currentTime
    val progressDescription = stringResource(R.string.player_progress)

    Column(modifier = modifier) {
        Slider(
            value = position.coerceIn(0f, duration.coerceAtLeast(0f)),
            onValueChange = {
                dragPosition = it
                onDraggingChange(true)
            },
            onValueChangeFinished = {
                dragPosition?.let(onSeek)
                dragPosition = null
                onDraggingChange(false)
            },
            valueRange = 0f..duration.coerceAtLeast(0f),
            enabled = duration > 0f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = progressDescription },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatPlaybackTime(position),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
            Text(
                text = formatPlaybackTime(duration),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
        }
    }
}
