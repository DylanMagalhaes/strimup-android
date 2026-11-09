package com.strimup.feature.home.presentation.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.core.ui.component.twitch.TwitchLiveCoverPlayer
import com.strimup.core.ui.component.twitch.twitchLivePreviewUrl
import com.strimup.core.ui.theme.RedLive
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.presentation.component.CutVideoPlayer
import com.strimup.feature.home.R
import com.strimup.feature.home.presentation.component.HomeHeroSkeleton
import com.strimup.feature.notification.presentation.bell.NotificationBell

private const val SCRIM_ALPHA = 0.75f
private const val TOP_SCRIM_ALPHA = 0.45f
private const val CONTROL_BACKGROUND_ALPHA = 0.35f
private const val INACTIVE_DOT_ALPHA = 0.4f
private const val MAX_DOTS = 8
private const val SECONDARY_TEXT_ALPHA = 0.85f
private const val GLASS_BUTTON_ALPHA = 0.18f
private val PillShape = RoundedCornerShape(percent = 50)

private val bottomScrim = Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = SCRIM_ALPHA)))
private val topScrim = Brush.verticalGradient(listOf(Color.Black.copy(alpha = TOP_SCRIM_ALPHA), Color.Transparent))

@Composable
fun HomeHero(
    state: HomeHeroUiState,
    isVisible: Boolean,
    unreadNotificationCount: Int?,
    onNotificationsClick: () -> Unit,
    onMuteClick: () -> Unit,
    onStreamerClick: (String) -> Unit,
    onWatchOnTwitchClick: (String) -> Unit,
    onCutClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(Color.Black)) {
        val fallbackCut = state.fallbackCut
        when {
            state.isLoading -> HomeHeroSkeleton()

            state.lives.isNotEmpty() -> LivePager(
                lives = state.lives,
                isVisible = isVisible,
                isMuted = state.isMuted,
                onStreamerClick = onStreamerClick,
                onWatchOnTwitchClick = onWatchOnTwitchClick,
            )

            fallbackCut != null -> HeroCut(
                cut = fallbackCut,
                isVisible = isVisible,
                isMuted = state.isMuted,
                onClick = { onCutClick(fallbackCut.id) },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(topScrim)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            if (state.lives.isNotEmpty() || fallbackCut != null) {
                HeroIconButton(
                    onClick = onMuteClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Icon(
                        imageVector = if (state.isMuted) {
                            Icons.AutoMirrored.Filled.VolumeOff
                        } else {
                            Icons.AutoMirrored.Filled.VolumeUp
                        },
                        contentDescription = stringResource(
                            if (state.isMuted) R.string.home_hero_sound_on else R.string.home_hero_sound_off,
                        ),
                    )
                }
            }
            unreadNotificationCount?.let { count ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = CONTROL_BACKGROUND_ALPHA)),
                ) {
                    NotificationBell(unreadCount = count, onClick = onNotificationsClick)
                }
            }
        }
    }
}

@Composable
private fun LivePager(
    lives: List<HeroLive>,
    isVisible: Boolean,
    isMuted: Boolean,
    onStreamerClick: (String) -> Unit,
    onWatchOnTwitchClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState { lives.size }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { page -> lives[page].channel },
        ) { page ->
            val live = lives[page]
            val isActive = isVisible && page == pagerState.settledPage
            Box(modifier = Modifier.fillMaxSize()) {
                if (isActive) {
                    TwitchLiveCoverPlayer(
                        channel = live.channel,
                        isPlaying = true,
                        isMuted = isMuted,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    AsyncImage(
                        model = twitchLivePreviewUrl(live.channel),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onStreamerClick(live.streamer.id) },
                )
                LiveInfo(
                    live = live,
                    onProfileClick = { onStreamerClick(live.streamer.id) },
                    onWatchOnTwitchClick = { onWatchOnTwitchClick(live.channel) },
                    modifier = Modifier.align(Alignment.BottomStart),
                )
            }
        }

        if (lives.size > 1) {
            PagerDots(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun LiveInfo(
    live: HeroLive,
    onProfileClick: () -> Unit,
    onWatchOnTwitchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bottomScrim)
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LiveBadge()
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AsyncImage(
                model = live.streamer.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = live.streamer.userName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = zalandoFontFamily,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                live.streamer.liveTitle?.takeIf { it.isNotBlank() }?.let { title ->
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeroPillButton(
                label = stringResource(R.string.home_hero_profile),
                containerColor = Color.White,
                contentColor = Color.Black,
                onClick = onProfileClick,
            )
            HeroPillButton(
                label = stringResource(R.string.home_hero_watch_on_twitch),
                containerColor = Color.White.copy(alpha = GLASS_BUTTON_ALPHA),
                contentColor = Color.White,
                onClick = onWatchOnTwitchClick,
            )
        }
    }
}

@Composable
private fun LiveBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(RedLive, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color.White, CircleShape),
        )
        Text(
            text = stringResource(R.string.home_hero_live),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun HeroPillButton(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        color = contentColor,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(PillShape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    )
}

@Composable
private fun HeroIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = CONTROL_BACKGROUND_ALPHA),
            contentColor = Color.White,
        ),
        content = content,
    )
}

@Composable
private fun PagerDots(pagerState: PagerState, modifier: Modifier = Modifier) {
    val count = minOf(pagerState.pageCount, MAX_DOTS)
    val selected = pagerState.currentPage % count
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(width = if (index == selected) 16.dp else 6.dp, height = 6.dp)
                    .background(
                        Color.White.copy(alpha = if (index == selected) 1f else INACTIVE_DOT_ALPHA),
                        CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun BoxScope.HeroCut(
    cut: Cut,
    isVisible: Boolean,
    isMuted: Boolean,
    onClick: () -> Unit,
) {
    CutVideoPlayer(
        cut = cut,
        isActive = isVisible,
        isPlaying = isVisible,
        isMuted = isMuted,
        fillBounds = true,
        modifier = Modifier.fillMaxSize(),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
    )
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .background(bottomScrim)
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = cut.author.username,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
        )
        HeroPillButton(
            label = stringResource(R.string.home_hero_open_cut),
            containerColor = Color.White,
            contentColor = Color.Black,
            onClick = onClick,
        )
    }
}
