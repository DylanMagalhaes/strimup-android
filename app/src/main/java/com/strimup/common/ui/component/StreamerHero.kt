package com.strimup.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily

@Composable
fun StreamerHero(
    isLive: Boolean,
    imageUrl: String,
    pseudo: String,
    tags: List<String>?,
    dailyStatus: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                AsyncImage(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                        .then(
                            if (isLive) {
                                Modifier.border(
                                    width = 2.5.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = CircleShape
                                )
                            } else Modifier
                        ),
                    model = imageUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )

                if (isLive) {
                    Surface(
                        modifier = Modifier.offset(y = 4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = "LIVE",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = zalandoFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(pseudo)
                        withStyle(
                            style = MaterialTheme.typography.titleLarge.toSpanStyle().copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append(".")
                        }
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = zalandoFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                )

                if (dailyStatus.isNotEmpty()) {
                    Text(
                        text = dailyStatus,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 16.sp
                    )
                }

                if (!tags.isNullOrEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        tags.forEach { tag ->
                            TagBadge(tag = tag)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StreamerHeroPreview() {
    StrimupTheme {
        StreamerHero(
            isLive = true,
            imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
            pseudo = "Squeezie",
            tags = listOf("Gaming", "Dev", "Cuisine"),
            dailyStatus = "En live toute la nuit sur le nouveau DLC !",
        )
    }
}