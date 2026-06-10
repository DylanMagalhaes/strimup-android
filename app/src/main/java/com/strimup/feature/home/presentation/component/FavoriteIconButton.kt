package com.strimup.feature.home.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.theme.StrimupTheme

@Composable
fun FavoriteIconButton(
    saved: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = null,

            )
    }
}

@Preview
@Composable
private fun FavoriteIconButtonPreview() {
    StrimupTheme {
        Row {
            FavoriteIconButton(
                saved = true,
                onClick = {},
            )

            FavoriteIconButton(
                saved = false,
                onClick = {},
            )
        }
    }
}
