package com.strimup.feature.profile.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
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
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity

@Composable
fun StreamerHero(
    isLive: Boolean,
    imageUrl: String,
    pseudo: String,
    tags: List<ProfileStreamerEntity.Tag>?,
    dailyStatus: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box() {
                AsyncImage(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = .4f)
                        )
                        .then(
                            if (isLive) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(8.dp),
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

            if(dailyStatus != ""){

                Row() {
                    Text(
                        text = " \" ",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = zalandoFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = dailyStatus,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = " \" ",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = zalandoFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }

            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tags?.forEach { tag ->
                    TagBadge(tag = tag.name)
                }
            }

            VerticalSpacer(4.dp)

            HorizontalDivider()
        }
    }
}

@Preview
@Composable
private fun StreamerHeroPreview() {
    StrimupTheme {

        StreamerHero(
            isLive = false,
            imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
            pseudo = "Squeezi",
            tags = listOf(
                ProfileStreamerEntity.Tag(name = "Gamming", category = "dolk"),
                ProfileStreamerEntity.Tag(name = "Dev", category = "dolk"),
                ProfileStreamerEntity.Tag(name = "Cuisine", category = "dolk"),
                ProfileStreamerEntity.Tag(name = "Cuisine", category = "dolk"),
            ),
            dailyStatus = "Hello la compagnie",
        )
    }
}