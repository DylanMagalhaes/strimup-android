package com.strimup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import com.strimup.core.navigation.Destination
import com.strimup.core.ui.component.streamer.YouTubePlayerScreen
import com.strimup.feature.auth.presentation.login.LoginScreen
import com.strimup.feature.favorite.presentation.FavoriteStreamerScreen
import com.strimup.feature.filter.presentation.navigation.FilterNavigation
import com.strimup.feature.home.presentation.navigation.HomeNavigation
import com.strimup.feature.search.presentation.navigation.SearchNavigation
import com.strimup.feature.streamerdetail.presentation.StreamerDetailScreen
import com.strimup.feature.streamerprofile.presentation.navigation.ProfileNavigation
import com.strimup.presentation.MainViewModel

@Composable
fun StrimupNavDisplay(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Destination.Home.StreamerList)
    val currentDestination = backStack.lastOrNull()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoggedIn = state.user != null
    val userId = state.user?.id

    val shouldHideBottomBar = currentDestination is Destination.StreamerDetail ||
            currentDestination is Destination.Login

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (!shouldHideBottomBar) {
                NavigationBar {
                    val isHomeSelected = currentDestination is Destination.Home
                    val isFilterSelected = currentDestination is Destination.Filter
                    val isSearchSelected = currentDestination is Destination.Search
                    val isFavoriteSelected = currentDestination is Destination.Favorite
                    val isProfileSelected = if (isLoggedIn) currentDestination is Destination.Profile else currentDestination is Destination.Login

                    val itemColors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    NavigationBarItem(
                        selected = isHomeSelected,
                        onClick = {
                            if (!isHomeSelected) {
                                backStack.add(Destination.Home.StreamerList)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = isFilterSelected,
                        onClick = {
                            if (!isFilterSelected) {
                                backStack.add(Destination.Filter.List)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Mes filtres",
                            )
                        },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = isSearchSelected,
                        onClick = {
                            if (!isSearchSelected) {
                                backStack.add(Destination.Search)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher",
                            )
                        },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = isFavoriteSelected,
                        onClick = {
                            if (!isFavoriteSelected) {
                                backStack.add(Destination.Favorite)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isFavoriteSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Mes streamers favoris",
                            )
                        },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = isProfileSelected,
                        onClick = {
                            if (isLoggedIn) {
                                val currentProfileUserId = (currentDestination as? Destination.Profile.View)?.userId
                                if (currentDestination !is Destination.Profile || currentProfileUserId != userId) {
                                    userId?.let { id ->
                                        backStack.add(Destination.Profile.View(userId = id))
                                    }
                                }
                            } else if (currentDestination !is Destination.Login) {
                                backStack.add(Destination.Login)
                            }
                        },
                        icon = {
                            if (isLoggedIn) {
                                AsyncImage(
                                    model = state.user?.avatarUrl,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .then(
                                            if (isProfileSelected) {
                                                Modifier
                                                    .border(
                                                        width = 2.dp,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        shape = CircleShape
                                                    )
                                                    .padding(2.dp)
                                            } else Modifier
                                        )
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .4f))
                                )
                            } else {
                                Icon(
                                    imageVector = if (isProfileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Connexion"
                                )
                            }
                        },
                        colors = itemColors
                    )
                }
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
                        }
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

                entry<Destination.YouTubePlayer>{ destination ->
                    YouTubePlayerScreen(
                        modifier = Modifier.fillMaxSize(),
                        videoId = destination.videoId,
                        isVertical = destination.isVertical,
                        onBack = { backStack.removeLastOrNull() }
                    )

                }

                entry<Destination.Favorite>{
                    FavoriteStreamerScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                entry<Destination.Login> {
                    LoginScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavToHome = {
                            backStack.clear()
                            backStack.add(Destination.Home.StreamerList)
                        }
                    )
                }
            }
        )
    }
}