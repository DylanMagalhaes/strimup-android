package com.strimup.feature.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.common.ui.component.button.SocialIconButton
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.home.domain.entity.StreamerEntity.Social
import com.strimup.feature.home.domain.entity.StreamerEntity.Social.Type
import com.strimup.feature.home.presentation.mapper.getIconRes

@Composable fun StreamerCard(
    pseudo: String,
    socials: List<Social>,
    saved: Boolean,
    imageUrl: String,
    isLive: Boolean,
    liveTitle: String?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSocialClick: (Social) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .padding(
                    start = 16.dp, end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box() {
                AsyncImage(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = .4f)
                        )
                        .then(
                            if (isLive) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        ),
                    model = imageUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )

                if (isLive) Badge(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 6.dp),
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    content = {
                        Text(
                            text = "Live",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = zalandoFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)

            ) {
                Text(
                    text = pseudo,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = zalandoFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                )
                if (liveTitle != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerticalDivider(
                            modifier = Modifier.size(16.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.tertiary
                        )


                        Text(
                            text = liveTitle,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,

                            )

                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    socials.forEach { social ->
                        SocialIconButton(
                            iconRes = social.getIconRes(), onClick = { onSocialClick(social) })
                    }
                }
            }

            FavoriteIconButton(
                saved = saved,
                onClick = onFavoriteClick,
            )
        }
    }
}

@Preview @Composable private fun StreamerCardPreview() {
    StrimupTheme {
        StreamerCard(
            modifier = Modifier.fillMaxWidth(),
            pseudo = "Mello",
            socials = listOf(Social(url = "", type = Type.Instagram)),
            saved = false,
            imageUrl = "",
            isLive = true,
            liveTitle = "lorem ipsum dolores ombrage",
            onClick = {},
            onSocialClick = {},
            onFavoriteClick = {},
        )
    }
}
