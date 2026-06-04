package com.strimup.common.ui.component.button

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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

enum class Social(val drawableRes: Int) {
    Twitch(drawableRes = R.drawable.ic_twitch),
    Youtube(drawableRes = R.drawable.ic_youtube),
    Kick(drawableRes = R.drawable.ic_kick),
    TikTok(drawableRes = R.drawable.ic_tiktok),
    Instagram(drawableRes = R.drawable.ic_instagram),
}

@Composable
fun SocialIconButton(
    social: Social,
    modifier: Modifier = Modifier,
    onClick: (Social) -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    IconButton(
        modifier = modifier
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = shape)
            .size(36.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = shape,
        onClick = { onClick(social) },
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(social.drawableRes),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun SocialIconButtonPreview() {
    StrimupTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Social.entries
                .forEach { socialIcon ->
                    SocialIconButton(
                        social = socialIcon,
                        onClick = {},
                    )
                }
        }
    }
}
