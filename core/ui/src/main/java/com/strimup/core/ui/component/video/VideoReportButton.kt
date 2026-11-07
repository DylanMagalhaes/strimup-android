package com.strimup.core.ui.component.video

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.R

private const val BUTTON_BACKGROUND_ALPHA = 0.6f

@Composable
internal fun VideoReportButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(32.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = BUTTON_BACKGROUND_ALPHA),
            contentColor = Color.White,
        ),
    ) {
        Icon(
            imageVector = Icons.Outlined.Flag,
            contentDescription = stringResource(R.string.video_report),
            modifier = Modifier.size(18.dp),
        )
    }
}
