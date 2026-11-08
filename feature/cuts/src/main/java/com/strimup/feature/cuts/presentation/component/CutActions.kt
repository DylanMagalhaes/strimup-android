package com.strimup.feature.cuts.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.theme.BrandPink
import com.strimup.feature.cuts.R

@Composable
internal fun CutActions(
    likesCount: Int,
    isLiked: Boolean,
    isLikeEnabled: Boolean,
    isMuted: Boolean,
    hasSoundControl: Boolean,
    onMuteClick: () -> Unit,
    onLikeClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (hasSoundControl) {
            CutActionButton(
                icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = stringResource(if (isMuted) R.string.cuts_sound_on else R.string.cuts_sound_off),
                onClick = onMuteClick,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CutActionButton(
                icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(if (isLiked) R.string.cuts_unlike else R.string.cuts_like),
                onClick = onLikeClick,
                enabled = isLikeEnabled,
                tint = if (isLiked) BrandPink else Color.White,
            )
            Text(
                text = formatLikesCount(likesCount),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        CutActionButton(
            icon = Icons.Outlined.Flag,
            contentDescription = stringResource(R.string.cuts_report),
            onClick = onReportClick,
        )
    }
}

@Composable
private fun CutActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.White,
) {
    IconButton(onClick = onClick, enabled = enabled, modifier = modifier.size(48.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else tint.copy(alpha = DISABLED_ALPHA),
            modifier = Modifier.size(32.dp),
        )
    }
}

private const val DISABLED_ALPHA = 0.4f
