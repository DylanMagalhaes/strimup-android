package com.strimup.feature.notification.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.ui.component.error.ErrorState
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.notification.domain.entity.Notification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

private const val LOAD_MORE_THRESHOLD = 3

@Composable
fun NotificationsScreen(
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotificationsUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }
            }
        }
    }

    NotificationsContent(
        modifier = modifier,
        state = state,
        snackBarHostState = snackBarHostState,
        onNavUp = onNavUp,
        onRetryClick = viewModel::onRetryClick,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onNotificationClick = viewModel::onNotificationClick,
        onDelete = viewModel::onDelete,
        onMarkAllAsReadClick = viewModel::onMarkAllAsReadClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsContent(
    state: NotificationsUiState,
    snackBarHostState: SnackbarHostState,
    onNavUp: () -> Unit,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onDelete: (Notification) -> Unit,
    onMarkAllAsReadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notifications_title),
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMarkAllAsReadClick, enabled = state.canMarkAllAsRead) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = stringResource(R.string.notifications_mark_all_as_read),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()

        when {
            state.isInitialLoading -> {
                Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.errorRes != null -> {
                ErrorState(modifier = contentModifier, messageRes = state.errorRes, onRetryClick = onRetryClick)
            }

            else -> {
                PullToRefreshBox(
                    modifier = contentModifier,
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                ) {
                    NotificationList(
                        state = state,
                        onLoadMore = onLoadMore,
                        onNotificationClick = onNotificationClick,
                        onDelete = onDelete,
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationList(
    state: NotificationsUiState,
    onLoadMore: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onDelete: (Notification) -> Unit,
) {
    val listState = rememberLazyListState()
    val now by produceState(initialValue = Instant.now()) {
        while (true) {
            delay(1.minutes)
            value = Instant.now()
        }
    }

    LoadMoreEffect(listState = listState, onLoadMore = onLoadMore)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        if (state.isEmpty) {
            item { EmptyNotifications(modifier = Modifier.fillParentMaxSize()) }
        }

        items(items = state.notifications, key = { it.id }) { notification ->
            Column(modifier = Modifier.animateItem()) {
                SwipeableNotificationItem(
                    notification = notification,
                    now = now,
                    onClick = { onNotificationClick(notification) },
                    onDelete = { onDelete(notification) },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        if (state.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun LoadMoreEffect(
    listState: LazyListState,
    onLoadMore: () -> Unit,
) {
    val isNearEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            layoutInfo.totalItemsCount > 0 && lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { isNearEnd }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
}

@Composable
private fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Icon(
            modifier = Modifier.size(56.dp),
            imageVector = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.notifications_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
