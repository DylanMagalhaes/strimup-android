package com.strimup.common.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.common.ui.theme.StrimupTheme

@Composable fun SocialIconButton(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    IconButton(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape
            )
            .size(36.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = shape,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
        )
    }
}

@Preview @Composable private fun SocialIconButtonPreview() {
    StrimupTheme {
        SocialIconButton(
            iconRes = R.drawable.ic_twitch,
            onClick = {},
        )
    }
}
