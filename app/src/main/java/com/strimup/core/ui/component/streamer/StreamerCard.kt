package com.strimup.core.ui.component.streamer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.R
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Social.Type
import com.strimup.core.streamer.domain.mapper.getIconRes
import com.strimup.core.ui.component.button.SocialIconButton
import com.strimup.core.ui.component.tag.TagBadge
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

private const val MAX_VISIBLE_TAGS = 4
private const val MAX_VISIBLE_SOCIALS = 3
private const val LIVE_GRADIENT_ALPHA = 0.10f
private const val LIVE_GRADIENT_WIDTH_RATIO = 0.65f
private const val AVATAR_PLACEHOLDER_ALPHA = 0.08f
private val CardShape = RoundedCornerShape(22.dp)
private val AvatarShape = RoundedCornerShape(16.dp)

@Composable
fun StreamerCard(
    pseudo: String,
    socials: List<Social>,
    imageUrl: String?,
    isLive: Boolean,
    liveTitle: String?,
    tags: List<String>,
    personality: String?,
    secondaryPersonality: String?,
    onClick: () -> Unit,
    onSocialClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val liveColor = MaterialTheme.colorScheme.tertiary
    val visibleLiveTitle = liveTitle?.takeIf { isLive && it.isNotBlank() }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (isLive) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(liveColor.copy(alpha = LIVE_GRADIENT_ALPHA), Color.Transparent),
                                start = Offset.Zero,
                                end = Offset(size.width * LIVE_GRADIENT_WIDTH_RATIO, size.height),
                            )
                        )
                    }
                }
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StreamerCardHeader(
                pseudo = pseudo,
                imageUrl = imageUrl,
                isLive = isLive,
                liveTitle = visibleLiveTitle,
            )

            if (tags.isNotEmpty()) {
                StreamerTags(tags = tags, onTagClick = onClick)
            }

            PersonalityLine(
                personality = personality,
                secondaryPersonality = secondaryPersonality,
            )

            if (isLive || socials.isNotEmpty()) {
                StreamerCardFooter(
                    isLive = isLive,
                    socials = socials,
                    onSocialClick = onSocialClick,
                )
            }
        }
    }
}

@Composable
private fun StreamerCardHeader(
    pseudo: String,
    imageUrl: String?,
    isLive: Boolean,
    liveTitle: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = if (liveTitle == null) Alignment.CenterVertically else Alignment.Top,
    ) {
        StreamerAvatar(imageUrl = imageUrl, isLive = isLive)

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = pseudo,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = zalandoFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (isLive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    LiveBadge()
                }
            }

            if (liveTitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LiveTitle(title = liveTitle)
            }
        }
    }
}

@Composable
private fun StreamerAvatar(
    imageUrl: String?,
    isLive: Boolean,
) {
    Box {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(AvatarShape)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = AVATAR_PLACEHOLDER_ALPHA))
                .then(
                    if (isLive) {
                        Modifier.border(width = 2.dp, color = MaterialTheme.colorScheme.tertiary, shape = AvatarShape)
                    } else {
                        Modifier
                    }
                ),
        )

        if (isLive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 5.dp, y = 5.dp)
                    .size(14.dp)
                    .background(color = MaterialTheme.colorScheme.tertiary, shape = CircleShape)
                    .border(width = 3.dp, color = MaterialTheme.colorScheme.surface, shape = CircleShape),
            )
        }
    }
}

@Composable
private fun LiveBadge() {
    Text(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        text = stringResource(R.string.streamer_live_badge),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.onTertiary,
    )
}

@Composable
private fun LiveTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(2.dp)),
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StreamerTags(
    tags: List<String>,
    onTagClick: () -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tags.take(MAX_VISIBLE_TAGS).forEach { tag ->
            TagBadge(tag = tag, onTagClick = onTagClick)
        }

        val hiddenTagsCount = tags.size - MAX_VISIBLE_TAGS
        if (hiddenTagsCount > 0) {
            TagBadge(tag = stringResource(R.string.streamer_more_tags, hiddenTagsCount), onTagClick = onTagClick)
        }
    }
}

@Composable
private fun StreamerCardFooter(
    isLive: Boolean,
    socials: List<Social>,
    onSocialClick: (String?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLive) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.streamer_live_now),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            socials.take(MAX_VISIBLE_SOCIALS).forEach { social ->
                SocialIconButton(
                    iconRes = social.getIconRes(),
                    onClick = { onSocialClick(social.url) },
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080808, widthDp = 420)
@Composable
internal fun StreamerCardOfflinePreview() {
    StrimupTheme {
        StreamerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            pseudo = "RaziU",
            socials = listOf(
                Social(url = "", type = Type.Twitch),
                Social(url = "", type = Type.Instagram),
                Social(url = "", type = Type.Youtube),
            ),
            imageUrl = "",
            isLive = false,
            liveTitle = "Ancien titre qui ne doit pas s'afficher",
            tags = listOf("FPS", "RPG", "MMORPG", "Énergique", "Fun"),
            personality = "Chill",
            secondaryPersonality = "Compétitif",
            onClick = {},
            onSocialClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080808, widthDp = 360)
@Composable
internal fun StreamerCardLivePreview() {
    StrimupTheme {
        StreamerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            pseudo = "UnPseudoVraimentTresTresLong",
            socials = listOf(Social(url = "", type = Type.Twitch)),
            imageUrl = "",
            isLive = true,
            liveTitle = "Ranked jusqu'au top 500, on ne lâche rien ce soir !",
            tags = listOf("FPS", "Compétitif"),
            personality = "Énergique",
            secondaryPersonality = null,
            onClick = {},
            onSocialClick = {},
        )
    }
}
