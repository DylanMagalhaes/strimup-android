package com.strimup.core.ui.component.streamer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Social.Type
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.mapper.getIconRes
import com.strimup.core.ui.R
import com.strimup.core.ui.component.button.SocialIconButton
import com.strimup.core.ui.component.tag.TagBadge
import com.strimup.core.ui.streamer.displayName
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private const val MAX_VISIBLE_TAGS = 4
private const val AVATAR_WIDTH_RATIO = 0.4f
private const val LIVE_GRADIENT_ALPHA = 0.10f
private const val LIVE_GRADIENT_WIDTH_RATIO = 0.65f
private const val AVATAR_PLACEHOLDER_ALPHA = 0.08f
private const val DIVIDER_ALPHA = 0.3f
private const val PREVIEW_NEXT_LIVE_HOURS = 3L
private val HeaderItemSpacing = 8.dp
private val CardShape = RoundedCornerShape(22.dp)
private val AvatarShape = RoundedCornerShape(16.dp)
private val FavoriteButtonShape = RoundedCornerShape(12.dp)

@Composable
fun StreamerCard(
    pseudo: String,
    socials: List<Social>,
    imageUrl: String?,
    isLive: Boolean,
    liveTitle: String?,
    tags: List<String>,
    nextLive: Streamer.NextLive?,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onSocialClick: (String?) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val liveColor = MaterialTheme.colorScheme.tertiary
    val visibleLiveTitle = liveTitle?.takeIf { isLive && it.isNotBlank() }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val nextLiveDisplay = remember(nextLive, isLive, today, zone) {
        if (isLive) null else nextLive?.toDisplay(today = today, zone = zone)
    }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StreamerCardHeader(
                imageUrl = imageUrl,
                isLive = isLive,
                tags = tags,
                isFavorite = isFavorite,
                onTagClick = onClick,
                onFavoriteClick = onFavoriteClick,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = DIVIDER_ALPHA))

            StreamerIdentity(pseudo = pseudo, liveTitle = visibleLiveTitle)

            if (socials.isNotEmpty()) {
                StreamerSocials(socials = socials, onSocialClick = onSocialClick)
            }

            if (nextLiveDisplay != null) {
                StreamerNextLiveCard(nextLive = nextLiveDisplay)
            }
        }
    }
}

@Composable
private fun StreamerCardHeader(
    imageUrl: String?,
    isLive: Boolean,
    tags: List<String>,
    isFavorite: Boolean,
    onTagClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val avatarSize = maxWidth * AVATAR_WIDTH_RATIO

        Row(modifier = Modifier.fillMaxWidth()) {
            StreamerAvatar(imageUrl = imageUrl, isLive = isLive, size = avatarSize)

            Spacer(modifier = Modifier.width(24.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(avatarSize),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(HeaderItemSpacing),
                ) {
                    StreamerTags(tags = tags, onTagClick = onTagClick)
                    FavoriteButton(isFavorite = isFavorite, onClick = onFavoriteClick)
                }
            }
        }
    }
}

@Composable
private fun StreamerAvatar(
    imageUrl: String?,
    isLive: Boolean,
    size: Dp,
) {
    Box {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
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
            LiveBadge(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            )

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
private fun StreamerTags(
    tags: List<String>,
    onTagClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(HeaderItemSpacing),
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
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(FavoriteButtonShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = stringResource(
                if (isFavorite) R.string.streamer_remove_favorite else R.string.streamer_add_favorite,
            ),
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StreamerIdentity(
    pseudo: String,
    liveTitle: String?,
) {
    val accentColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append(pseudo.uppercase())
                withStyle(SpanStyle(color = accentColor)) { append(".") }
            },
            style = MaterialTheme.typography.titleLarge,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (liveTitle != null) {
            LiveTitle(title = liveTitle)
        }
    }
}

@Composable
private fun StreamerSocials(
    socials: List<Social>,
    onSocialClick: (String?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        socials.forEach { social ->
            SocialIconButton(
                iconRes = social.getIconRes(),
                contentDescription = stringResource(R.string.streamer_open_social, social.type.displayName()),
                onClick = { onSocialClick(social.url) },
            )
        }
    }
}

private val previewNextLive = Streamer.NextLive(
    title = "Valorant ranked",
    startsAt = Instant.now().plus(Duration.ofHours(PREVIEW_NEXT_LIVE_HOURS)),
)

@Preview(showBackground = true, backgroundColor = 0xFF080808, widthDp = 420)
@Composable
internal fun StreamerCardOfflinePreview() {
    StrimupTheme {
        StreamerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            pseudo = "LeBreakStudio",
            socials = listOf(
                Social(url = "", type = Type.Twitch),
                Social(url = "", type = Type.Instagram),
                Social(url = "", type = Type.Youtube),
                Social(url = "", type = Type.Tiktok),
            ),
            imageUrl = "",
            isLive = false,
            liveTitle = "Ancien titre qui ne doit pas s'afficher",
            tags = listOf("FPS", "RPG", "MMORPG", "Énergique"),
            nextLive = previewNextLive,
            isFavorite = false,
            onClick = {},
            onSocialClick = {},
            onFavoriteClick = {},
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
            nextLive = previewNextLive,
            isFavorite = true,
            onClick = {},
            onSocialClick = {},
            onFavoriteClick = {},
        )
    }
}
