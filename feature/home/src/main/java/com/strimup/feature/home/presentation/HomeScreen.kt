package com.strimup.feature.home.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.ui.browser.rememberExternalLinkOpener
import com.strimup.core.ui.component.streamer.StreamerCard
import com.strimup.core.ui.component.twitch.rememberTwitchChannelOpener
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.BannerType
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.presentation.component.HomeBanner
import com.strimup.feature.home.presentation.component.HomeBannerSkeleton
import com.strimup.feature.home.presentation.component.HomeCutsRowSkeleton
import com.strimup.feature.home.presentation.component.HomeOfflineBanner
import com.strimup.feature.home.presentation.component.HomeStreamersError
import com.strimup.feature.home.presentation.component.HomeTabs
import com.strimup.feature.home.presentation.component.StreamerCardSkeleton
import com.strimup.feature.home.presentation.hero.HomeCutsRow
import com.strimup.feature.home.presentation.hero.HomeHero
import com.strimup.feature.home.presentation.hero.HomeHeroUiState
import com.strimup.feature.home.presentation.hero.HomeHeroViewModel
import com.strimup.core.ui.R as CoreUiR

private const val HERO_SCREEN_RATIO = 0.45f
private const val HERO_VISIBLE_RATIO = 0.5f
private const val HERO_ITEM_INDEX = 0
private const val STREAMER_SKELETON_COUNT = 3

data class HomeHeroActions(
    val onMuteClick: () -> Unit,
    val onWatchOnTwitchClick: (String) -> Unit,
    val onCutClick: (String) -> Unit,
    val onSeeAllCutsClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onStreamerClick: (id: String) -> Unit,
    onStreamerBannerClick: (String?) -> Unit,
    unreadNotificationCount: Int?,
    onNotificationsClick: () -> Unit,
    isLoggedIn: Boolean,
    onLoginRequired: () -> Unit,
    onCutClick: (String) -> Unit,
    onSeeAllCutsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    heroViewModel: HomeHeroViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val heroState by heroViewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val openExternalLink = rememberExternalLinkOpener(snackBarHostState)
    val openTwitchChannel = rememberTwitchChannelOpener(snackBarHostState)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }
            }
        }
    }

    HomeContent(
        modifier = modifier.fillMaxSize(),
        state = state,
        heroState = heroState,
        heroActions = HomeHeroActions(
            onMuteClick = heroViewModel::onMuteClick,
            onWatchOnTwitchClick = openTwitchChannel::invoke,
            onCutClick = onCutClick,
            onSeeAllCutsClick = onSeeAllCutsClick,
        ),
        snackBarHostState = snackBarHostState,
        onStreamerClick = onStreamerClick,
        onSocialClick = openExternalLink::invoke,
        onFavoriteClick = { streamerId ->
            if (isLoggedIn) viewModel.onFavoriteClick(streamerId) else onLoginRequired()
        },
        onTabClick = viewModel::onTabClick,
        onRetryClick = viewModel::onRetryClick,
        onRefresh = {
            viewModel.onRefresh()
            heroViewModel.refresh()
        },
        unreadNotificationCount = unreadNotificationCount,
        onNotificationsClick = onNotificationsClick,
        onBannerClick = { banner ->
            when {
                banner.linkUrl.isNullOrBlank() -> Unit
                banner.type == BannerType.FeaturedStreamer -> onStreamerBannerClick(banner.streamerId)
                else -> openExternalLink(banner.linkUrl)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState,
    heroState: HomeHeroUiState,
    heroActions: HomeHeroActions,
    snackBarHostState: SnackbarHostState,
    onStreamerClick: (id: String) -> Unit,
    onBannerClick: (BannerItemEntity) -> Unit,
    onSocialClick: (String?) -> Unit,
    onFavoriteClick: (id: String) -> Unit,
    onTabClick: (FilterEntity) -> Unit,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    unreadNotificationCount: Int?,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val heroHeight = LocalConfiguration.current.screenHeightDp.dp * HERO_SCREEN_RATIO
    val isHeroVisible by rememberIsHeroVisible(lazyListState, heroHeight)
    val isHeroScrolledAway by remember { derivedStateOf { lazyListState.firstVisibleItemIndex > HERO_ITEM_INDEX } }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { padding ->
        Surface(
            modifier = Modifier.padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item(key = "hero") {
                        HomeHero(
                            state = heroState,
                            isVisible = isHeroVisible,
                            unreadNotificationCount = unreadNotificationCount,
                            onNotificationsClick = onNotificationsClick,
                            onMuteClick = heroActions.onMuteClick,
                            onStreamerClick = onStreamerClick,
                            onWatchOnTwitchClick = heroActions.onWatchOnTwitchClick,
                            onCutClick = heroActions.onCutClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heroHeight),
                        )
                    }

                    if (heroState.isLoading) {
                        item(key = "cuts_skeleton") {
                            HomeCutsRowSkeleton(modifier = Modifier.padding(top = 8.dp))
                        }
                    } else if (heroState.cuts.isNotEmpty()) {
                        item(key = "cuts") {
                            HomeCutsRow(
                                cuts = heroState.cuts,
                                onCutClick = heroActions.onCutClick,
                                onSeeAllClick = heroActions.onSeeAllCutsClick,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }

                    if (state.bannerItems.isNotEmpty()) {
                        item(key = "banner") {
                            HomeBanner(banners = state.bannerItems, onBannerClick = onBannerClick)
                        }
                    } else if (state.isBannerLoading) {
                        item(key = "banner_skeleton") {
                            HomeBannerSkeleton()
                        }
                    }

                    stickyHeader(key = "tabs") {
                        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
                            HomeTabs(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = if (isHeroScrolledAway) statusBarHeight() else 0.dp)
                                    .padding(vertical = 8.dp),
                                onButtonClick = onTabClick,
                                currentTab = state.currentTab,
                            )
                        }
                    }

                    homeStreamers(
                        state = state,
                        onStreamerClick = onStreamerClick,
                        onSocialClick = onSocialClick,
                        onFavoriteClick = onFavoriteClick,
                        onRetryClick = onRetryClick,
                    )
                }
            }
        }
    }
}

private fun LazyListScope.homeStreamers(
    state: HomeUiState,
    onStreamerClick: (id: String) -> Unit,
    onSocialClick: (String?) -> Unit,
    onFavoriteClick: (id: String) -> Unit,
    onRetryClick: () -> Unit,
) {
    if (state.isLoading) {
        items(count = STREAMER_SKELETON_COUNT, key = { index -> "streamer_skeleton_$index" }) {
            StreamerCardSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }
        return
    }

    if (state.isShowingSavedContent) {
        item(key = "offline") {
            HomeOfflineBanner(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }

    if (state.shouldShowStreamersError) {
        item(key = "error") {
            HomeStreamersError(
                messageRes = state.errorMessageRes ?: CoreUiR.string.error_unknown,
                onRetryClick = onRetryClick,
            )
        }
    }

    items(items = state.streamers, key = { streamer -> streamer.id }) { streamer ->
        StreamerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .defaultMinSize(minHeight = 112.dp),
            pseudo = streamer.userName,
            socials = streamer.socials,
            imageUrl = streamer.imageUrl,
            isLive = streamer.isLive,
            liveTitle = streamer.liveTitle,
            onClick = { onStreamerClick(streamer.id) },
            onSocialClick = onSocialClick,
            onFavoriteClick = { onFavoriteClick(streamer.id) },
            tags = streamer.tags.orEmpty().map { it.name },
            nextLive = streamer.nextLive,
            isFavorite = streamer.id in state.favoriteStreamerIds,
        )
    }
}

@Composable
private fun rememberIsHeroVisible(lazyListState: LazyListState, heroHeight: Dp) = with(LocalDensity.current) {
    val visibleLimitPx = heroHeight.toPx() * HERO_VISIBLE_RATIO
    remember(lazyListState, visibleLimitPx) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == HERO_ITEM_INDEX &&
                lazyListState.firstVisibleItemScrollOffset < visibleLimitPx
        }
    }
}

@Composable
private fun statusBarHeight(): Dp = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }

@Composable
@Preview
private fun HomeScreenPreview() {
    StrimupTheme {
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = HomeUiState(),
            heroState = HomeHeroUiState(isLoading = false),
            heroActions = HomeHeroActions(
                onMuteClick = {},
                onWatchOnTwitchClick = {},
                onCutClick = {},
                onSeeAllCutsClick = {},
            ),
            snackBarHostState = remember { SnackbarHostState() },
            onStreamerClick = {},
            onSocialClick = {},
            onFavoriteClick = {},
            onTabClick = {},
            onRetryClick = {},
            onRefresh = {},
            onBannerClick = {},
            unreadNotificationCount = 3,
            onNotificationsClick = {},
        )
    }
}
