package com.strimup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.strimup.core.navigation.Destination2
import com.strimup.feature.auth.presentation.login.LoginScreen
import com.strimup.feature.filter.presentation.navigation.FilterNavigation
import com.strimup.feature.home.presentation.navigation.HomeNavigation
import com.strimup.feature.search.presentation.navigation.SearchNavigation
import com.strimup.feature.streamerdetail.presentation.StreamerDetailScreen
import com.strimup.feature.streamerprofile.presentation.navigation.ProfileNavigation
import com.strimup.presentation.MainViewModel

fun Modifier.boldOnSelection(isSelected: Boolean, strokeWidthDp: Float = 0.8f): Modifier = this.then(
    if (isSelected) {
        this.drawWithContent {
            drawContent()
            drawContent()
        }
    } else Modifier
)

@Composable
fun StrimupNavDisplay2(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Destination2.Home.StreamerList)
    val currentDestination = backStack.lastOrNull()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoggedIn = state.user != null
    val userId = state.user?.id

    val shouldHideBottomBar = currentDestination is Destination2.StreamerDetail ||
            currentDestination is Destination2.Login

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (!shouldHideBottomBar) {
                NavigationBar {
                    val isHomeSelected = currentDestination is Destination2.Home
                    val isFilterSelected = currentDestination is Destination2.Filter
                    val isSearchSelected = currentDestination is Destination2.Search
                    val isProfileSelected = if (isLoggedIn) currentDestination is Destination2.Profile else currentDestination is Destination2.Login

                    val itemColors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    NavigationBarItem(
                        selected = isHomeSelected,
                        onClick = {
                            if (!isHomeSelected) {
                                backStack.add(Destination2.Home.StreamerList)
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
                                backStack.add(Destination2.Filter.List)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Mes filtres",
                                modifier = Modifier.boldOnSelection(isFilterSelected)
                            )
                        },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = isSearchSelected,
                        onClick = {
                            if (!isSearchSelected) {
                                backStack.add(Destination2.Search)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher",
                                modifier = Modifier.boldOnSelection(isSearchSelected)
                            )
                        },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = isProfileSelected,
                        onClick = {
                            if (isLoggedIn) {
                                val currentProfileUserId = (currentDestination as? Destination2.Profile.View)?.userId
                                if (currentDestination !is Destination2.Profile || currentProfileUserId != userId) {
                                    userId?.let { id ->
                                        backStack.add(Destination2.Profile.View(userId = id))
                                    }
                                }
                            } else if (currentDestination !is Destination2.Login) {
                                backStack.add(Destination2.Login)
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
                entry<Destination2.Home.StreamerList> {
                    HomeNavigation(
                        modifier = Modifier.fillMaxSize(),
                        onStreamerBannerClick = { streamerId ->
                            backStack.add(Destination2.StreamerDetail(streamerId = streamerId ?: ""))
                        },
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination2.StreamerDetail(streamerId = streamerId))
                        }
                    )
                }

                entry<Destination2.Search> {
                    SearchNavigation(
                        modifier = Modifier.fillMaxSize(),
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination2.StreamerDetail(streamerId = streamerId))
                        }
                    )
                }

                entry<Destination2.Filter.List> {
                    FilterNavigation(
                        onStreamerClick = { streamerId ->
                            backStack.add(Destination2.StreamerDetail(streamerId = streamerId))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                entry<Destination2.Profile.View> { destination ->
                    ProfileNavigation(
                        userId = destination.userId,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                entry<Destination2.StreamerDetail> { destination ->
                    StreamerDetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        streamerId = destination.streamerId,
                        onNavUp = { backStack.removeLastOrNull() },
                    )
                }

                entry<Destination2.Login> {
                    LoginScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavToHome = {
                            backStack.clear()
                            backStack.add(Destination2.Home.StreamerList)
                        }
                    )
                }
            }
        )
    }
}