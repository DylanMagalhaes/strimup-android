package com.strimup.feature.home.presentation

import android.content.ActivityNotFoundException
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.ui.component.streamer.StreamerCard
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.presentation.component.HomeBanner
import com.strimup.feature.home.presentation.component.HomeTabs
import com.strimup.feature.notification.presentation.bell.NotificationBell
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onStreamerClick: (id: String) -> Unit,
    onStreamerBannerClick: (String?) -> Unit,
    unreadNotificationCount: Int?,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()

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
        snackBarHostState = snackBarHostState,
        onStreamerClick = onStreamerClick,
        onSocialClick = { socialUrl ->
            if (!socialUrl.isNullOrBlank()) {
                try {
                    uriHandler.openUri(socialUrl)
                } catch (_: ActivityNotFoundException) {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(resources.getString(R.string.error_open_link))
                    }
                }
            }
        },
        onTabClick = viewModel::onTabClick,
        unreadNotificationCount = unreadNotificationCount,
        onNotificationsClick = onNotificationsClick,
        onBannerClick = { banner ->
            if (!banner.linkUrl.isNullOrBlank()) {
                try {
                    if (banner.type == "FEATURED_STREAMER") {
                        onStreamerBannerClick(banner.streamerId)
                    } else {
                        uriHandler.openUri(banner.linkUrl)
                    }
                } catch (_: ActivityNotFoundException) {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(resources.getString(R.string.error_open_link))
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    state: HomeUiState,
    snackBarHostState: SnackbarHostState,
    onStreamerClick: (id: String) -> Unit,
    onBannerClick: (BannerItemEntity) -> Unit,
    onSocialClick: (String?) -> Unit,
    onTabClick: (FilterEntity) -> Unit,
    unreadNotificationCount: Int?,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { padding ->
        Surface(
            modifier = Modifier.padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            ) {
                HomeTopBar(
                    unreadNotificationCount = unreadNotificationCount,
                    onNotificationsClick = onNotificationsClick,
                )

                Crossfade(targetState = state.isLoading, label = "loading_crossfade") { isLoading ->
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    } else {
                        val lazyListState = rememberLazyListState()
                        val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

                        LazyColumn(
                            state = lazyListState,
                            flingBehavior = snapFlingBehavior,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            item {
                                HomeBanner(
                                    banners = state.bannerItems,
                                    onBannerClick = onBannerClick
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            stickyHeader {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    HomeTabs(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        onButtonClick = onTabClick,
                                        currentTab = state.currentTab,
                                    )
                                }
                            }

                            items(
                                items = state.streamers,
                                key = { streamer -> streamer.id }
                            ) { streamer ->
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
                                    tags = streamer.tags.orEmpty().map { it.name },
                                    personality = streamer.personality,
                                    secondaryPersonality = streamer.personalitySecondary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    unreadNotificationCount: Int?,
    onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        unreadNotificationCount?.let { count ->
            NotificationBell(unreadCount = count, onClick = onNotificationsClick)
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    StrimupTheme {
        HomeContent(
            modifier = Modifier.fillMaxSize(),
            state = HomeUiState(),
            snackBarHostState = remember { SnackbarHostState() },
            onStreamerClick = {},
            onSocialClick = {},
            onTabClick = {},
            onBannerClick = {},
            unreadNotificationCount = 3,
            onNotificationsClick = {},
        )
    }
}