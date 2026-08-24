package com.strimup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.strimup.core.navigation.Destination2
import com.strimup.feature.auth.presentation.login.LoginScreen
import com.strimup.feature.filter.presentation.navigation.FilterNavigation
import com.strimup.feature.home.presentation.navigation.HomeNavigation
import com.strimup.feature.search.presentation.navigation.SearchNavigation
import com.strimup.feature.streamerdetail.presentation.StreamerDetailScreen
import com.strimup.feature.streamerprofile.presentation.navigation.ProfileNavigation
import com.strimup.presentation.MainViewModel

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
                    val itemColors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary,
                    )

                    NavigationBarItem(
                        selected = currentDestination is Destination2.Home,
                        onClick = {
                            if (currentDestination !is Destination2.Home) {
                                backStack.add(Destination2.Home.StreamerList)
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = currentDestination is Destination2.Filter,
                        onClick = {
                            if (currentDestination !is Destination2.Filter) {
                                backStack.add(Destination2.Filter.List)
                            }
                        },
                        icon = { Icon(Icons.Default.Tune, contentDescription = "Mes filtres") },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = currentDestination is Destination2.Search,
                        onClick = {
                            if (currentDestination !is Destination2.Search) {
                                backStack.add(Destination2.Search)
                            }
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Rechercher") },
                        colors = itemColors
                    )

                    NavigationBarItem(
                        selected = if (isLoggedIn) currentDestination is Destination2.Profile else currentDestination is Destination2.Login,
                        onClick = {
                            if (isLoggedIn) {
                                if (currentDestination !is Destination2.Profile) {
                                    backStack.add(Destination2.Profile.View(userId = userId))
                                }
                            } else if (currentDestination !is Destination2.Login) {
                                backStack.add(Destination2.Login)
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = if (isLoggedIn) "Profile" else "Connexion"
                            )
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