package com.strimup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import com.strimup.core.navigation.Destination
import com.strimup.core.navigation.navigateAsTab
import com.strimup.core.ui.component.streamer.YouTubePlayerScreen
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.account.presentation.account.AccountScreen
import com.strimup.feature.account.presentation.deletion.DeleteAccountScreen
import com.strimup.feature.auth.presentation.login.LoginScreen
import com.strimup.feature.auth.presentation.oauthonboarding.OAuthOnboardingScreen
import com.strimup.feature.auth.presentation.register.RegisterScreen
import com.strimup.feature.favorite.presentation.FavoriteStreamerScreen
import com.strimup.feature.filter.presentation.navigation.FilterNavigation
import com.strimup.feature.home.presentation.navigation.HomeNavigation
import com.strimup.feature.notification.presentation.list.NotificationsScreen
import com.strimup.feature.push.presentation.permission.NotificationPermissionPrompt
import com.strimup.feature.search.presentation.navigation.SearchNavigation
import com.strimup.feature.streamerdetail.presentation.StreamerDetailScreen
import com.strimup.feature.streamerprofile.presentation.navigation.ProfileNavigation
import com.strimup.presentation.MainUiEvent
import com.strimup.presentation.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun StrimupNavDisplay(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Destination.Home.StreamerList)
    val currentDestination = backStack.lastOrNull()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val isLoggedIn = state.user != null
    val userId = state.user?.id
    val userRole = state.user?.role

    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            handleMainUiEvent(
                event = event,
                backStack = backStack,
                showSnackBar = { textRes -> snackBarHostState.showSnackbar(resources.getString(textRes)) },
            )
        }
    }

    val shouldHideBottomBar = currentDestination is Destination.StreamerDetail ||
            currentDestination is Destination.Login ||
            currentDestination is Destination.Register ||
            currentDestination is Destination.OAuthOnboarding ||
            currentDestination is Destination.DeleteAccount ||
            currentDestination is Destination.Notifications

    val shouldAskNotificationPermission by viewModel.shouldAskNotificationPermission.collectAsStateWithLifecycle()
    NotificationPermissionPrompt(
        shouldAsk = shouldAskNotificationPermission && !currentDestination.isAuthFlow(),
        onHandled = viewModel::onNotificationPermissionHandled,
    )

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (!shouldHideBottomBar) {
                StrimupBottomBar(
                    currentDestination = currentDestination,
                    isLoggedIn = isLoggedIn,
                    userId = userId,
                    avatarUrl = state.user?.avatarUrl,
                    userRole = userRole ?: UserRole.VIEWER,
                    onNavigateAsTab = { destination -> backStack.navigateAsTab(destination) },
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Destination.Home.StreamerList> {
                    HomeNavigation(
                        modifier = Modifier.fillMaxSize(),
                        onStreamerBannerClick = { streamerId ->
                            backStack.add(Destination.StreamerDetail(streamerId = streamerId ?: ""))
                        },
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination.StreamerDetail(streamerId = streamerId))
                        },
                        unreadNotificationCount = unreadNotificationCount.takeIf { isLoggedIn },
                        onNotificationsClick = { backStack.add(Destination.Notifications) },
                    )
                }

                entry<Destination.Search> {
                    SearchNavigation(
                        modifier = Modifier.fillMaxSize(),
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination.StreamerDetail(streamerId = streamerId))
                        }
                    )
                }

                entry<Destination.Filter.List> {
                    FilterNavigation(
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination.StreamerDetail(streamerId = streamerId))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                entry<Destination.Profile.View> { destination ->
                    ProfileNavigation(
                        userId = destination.userId,
                        modifier = Modifier.fillMaxSize(),
                        onAccountNav = { backStack.add(Destination.Account) },
                    )
                }

                entry<Destination.StreamerDetail> { destination ->
                    StreamerDetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        streamerId = destination.streamerId,
                        onVideoClick = { videoId, isVertical ->
                            backStack.add(Destination.YouTubePlayer(videoId, isVertical))
                        },
                        onNavUp = { backStack.removeLastOrNull() },
                    )
                }

                entry<Destination.YouTubePlayer> { destination ->
                    YouTubePlayerScreen(
                        modifier = Modifier.fillMaxSize(),
                        videoId = destination.videoId,
                        isVertical = destination.isVertical,
                        onBack = { backStack.removeLastOrNull() }
                    )

                }

                entry<Destination.Favorite> {
                    FavoriteStreamerScreen(
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination.StreamerDetail(streamerId = streamerId))

                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                entry<Destination.Login> {
                    LoginScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavToHome = {
                            backStack.clear()
                            backStack.add(Destination.Home.StreamerList)
                        },
                        onNavToRegister = {
                            backStack.clear()
                            backStack.add(Destination.Register)
                        },
                    )
                }

                entry<Destination.Register> {
                    RegisterScreen(
                        onNavToHome = {
                            backStack.clear()
                            backStack.add(Destination.Home.StreamerList)
                        },
                        modifier = Modifier.fillMaxSize(),
                        onNavToLogin = { backStack.removeLastOrNull() }
                    )
                }

                entry<Destination.Account> {
                    val accountNavUp: (() -> Unit)? = when (userRole) {
                        UserRole.VIEWER -> null
                        else -> {
                            { backStack.removeLastOrNull() }
                        }
                    }
                    AccountScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavUp = accountNavUp,
                        onDeleteAccountNav = { backStack.add(Destination.DeleteAccount) },
                        onLoggedOut = {
                            backStack.clear()
                            backStack.add(Destination.Login)
                        },
                    )
                }

                entry<Destination.Notifications> {
                    NotificationsScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavUp = { backStack.removeLastOrNull() },
                    )
                }

                entry<Destination.DeleteAccount> {
                    DeleteAccountScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavUp = { backStack.removeLastOrNull() },
                        onAccountDeleted = {
                            backStack.clear()
                            backStack.add(Destination.Home.StreamerList)
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    resources.getString(R.string.account_deletion_success)
                                )
                            }
                        },
                    )
                }

                entry<Destination.OAuthOnboarding> { destination ->
                    OAuthOnboardingScreen(
                        tmp = destination.tmp,
                        modifier = Modifier.fillMaxSize(),
                        onCompleted = {
                            backStack.clear()
                            backStack.add(Destination.Home.StreamerList)
                        },
                    )
                }
            }
        )
    }
}

private fun NavKey?.isAuthFlow(): Boolean =
    this is Destination.Login || this is Destination.Register || this is Destination.OAuthOnboarding

private suspend fun handleMainUiEvent(
    event: MainUiEvent,
    backStack: NavBackStack<NavKey>,
    showSnackBar: suspend (Int) -> Unit,
) {
    when (event) {
        MainUiEvent.OAuthLoggedIn -> {
            backStack.clear()
            backStack.add(Destination.Home.StreamerList)
        }

        MainUiEvent.OpenNotifications -> {
            if (backStack.lastOrNull() != Destination.Notifications) {
                backStack.add(Destination.Notifications)
            }
        }

        is MainUiEvent.OAuthOnboardingRequired -> {
            backStack.add(Destination.OAuthOnboarding(tmp = event.tmp))
        }

        is MainUiEvent.ShowSnackBar -> showSnackBar(event.textRes)
    }
}

@Composable
private fun StrimupBottomBar(
    currentDestination: NavKey?,
    isLoggedIn: Boolean,
    userId: String?,
    userRole: UserRole,
    avatarUrl: String?,
    onNavigateAsTab: (Destination) -> Unit,
) {
    val isHomeSelected = currentDestination is Destination.Home
    val isFilterSelected = currentDestination is Destination.Filter
    val isSearchSelected = currentDestination is Destination.Search
    val isFavoriteSelected = currentDestination is Destination.Favorite
    val isProfileSelected = if (isLoggedIn) {
        currentDestination is Destination.Profile || currentDestination is Destination.Account
    } else {
        currentDestination is Destination.Login
    }

    CompactNavigationBar {
        CompactNavigationBarItem(
            selected = isHomeSelected,
            onClick = { onNavigateAsTab(Destination.Home.StreamerList) },
            icon = {
                Icon(
                    imageVector = if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = stringResource(R.string.nav_home)
                )
            }
        )

        CompactNavigationBarItem(
            selected = isFilterSelected,
            onClick = {
                onNavigateAsTab(if (isLoggedIn) Destination.Filter.List else Destination.Login)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = stringResource(R.string.filters_title),
                )
            }
        )

        CompactNavigationBarItem(
            selected = isSearchSelected,
            onClick = { onNavigateAsTab(Destination.Search) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.nav_search),
                )
            }
        )

        CompactNavigationBarItem(
            selected = isFavoriteSelected,
            onClick = {
                onNavigateAsTab(if (isLoggedIn) Destination.Favorite else Destination.Login)
            },
            icon = {
                Icon(
                    imageVector = if (isFavoriteSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = stringResource(R.string.nav_favorites),
                )
            }
        )

        CompactNavigationBarItem(
            selected = isProfileSelected,
            onClick = {
                when {
                    isLoggedIn && userRole == UserRole.VIEWER -> onNavigateAsTab(Destination.Account)
                    isLoggedIn && userId != null -> onNavigateAsTab(Destination.Profile.View(userId = userId))
                    else -> onNavigateAsTab(Destination.Login)
                }
            },
            icon = {
                ProfileNavigationIcon(
                    isLoggedIn = isLoggedIn,
                    isSelected = isProfileSelected,
                    avatarUrl = avatarUrl,
                    userRole = userRole
                )
            }
        )
    }
}

@Composable
private fun CompactNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = NavigationBarDefaults.containerColor,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
private fun RowScope.CompactNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = null,
                indication = ripple(bounded = false),
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            icon()
        }
    }
}

@Composable
private fun ProfileNavigationIcon(
    isLoggedIn: Boolean,
    isSelected: Boolean,
    userRole: UserRole,
    avatarUrl: String?,
) {
    if (!isLoggedIn) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Person else Icons.Outlined.Person,
            contentDescription = stringResource(R.string.login_title)
        )
        return
    }

    if (userRole == UserRole.VIEWER) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
            contentDescription = stringResource(R.string.account_title)
        )
        return
    }

    AsyncImage(
        model = avatarUrl,
        contentDescription = stringResource(R.string.nav_profile),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(24.dp)
            .then(
                if (isSelected) {
                    Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(2.dp)
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .4f))
    )
}