package com.strimup

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.strimup.common.navigation.Destination
import com.strimup.feature.auth.presentation.login.LoginScreen
import com.strimup.feature.home.presentation.HomeScreen
import com.strimup.feature.search.presentation.SearchScreen
import com.strimup.feature.streamerdetail.presentation.StreamerDetailScreen
import com.strimup.feature.streamerprofile.presentation.editeprofile.EditProfileScreen
import com.strimup.feature.streamerprofile.presentation.editeprofile.EditProfileViewModel
import com.strimup.feature.streamerprofile.presentation.editeprofile.SelectTagsScreen
import com.strimup.feature.streamerprofile.presentation.streamerprofile.StreamerProfileScreen
import com.strimup.presentation.MainViewModel

@Composable
fun StrimupNavDisplay(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Destination.Home)
    val currentDestination = backStack.lastOrNull()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoggedIn = state.user != null
    val userId = state.user?.id

    val shouldHideBottomBar = currentDestination is Destination.StreamerDetail ||
            currentDestination is Destination.StreamerEditProfile

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (!shouldHideBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination == Destination.Home,
                        onClick = {
                            if (currentDestination != Destination.Home) {
                                backStack.add(Destination.Home)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        }
                    )

                    NavigationBarItem(
                        selected = currentDestination == Destination.NewFilter,
                        onClick = {
                            if (currentDestination != Destination.NewFilter) {
                                backStack.add(Destination.NewFilter)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Créer un filtre"
                            )
                        }
                    )

                    NavigationBarItem(
                        selected = currentDestination == Destination.Search,
                        onClick = {
                            if (currentDestination != Destination.Search) {
                                backStack.add(Destination.Search)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher"
                            )
                        }
                    )

                    NavigationBarItem(
                        selected = if (isLoggedIn) {
                            currentDestination is Destination.StreamerProfile
                        } else {
                            currentDestination == Destination.Login
                        },
                        onClick = {
                            if (isLoggedIn) {
                                userId?.let { id ->
                                    if (currentDestination !is Destination.StreamerProfile) {
                                        backStack.add(Destination.StreamerProfile(streamerId = id))
                                    }
                                }
                            } else if (currentDestination != Destination.Login) {
                                backStack.add(Destination.Login)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = if (isLoggedIn) "Profile" else "Connexion"
                            )
                        }
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
                entry<Destination.Home> {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        onStreamerClick = { id ->
                            backStack.add(Destination.StreamerDetail(streamerId = id))
                        },
                    )
                }

                entry<Destination.Login> {
                    LoginScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavToHome = {
                            backStack.clear()
                            backStack.add(Destination.Home)
                        }
                    )
                }

                entry<Destination.StreamerDetail> {
                    StreamerDetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        streamerId = it.streamerId,
                        onNavUp = { backStack.removeLastOrNull() },
                    )
                }

                entry<Destination.StreamerProfile> {
                    StreamerProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavUp = { backStack.removeLastOrNull() },
                        onEditProfileNav = { backStack.add(Destination.StreamerEditProfile) }
                    )
                }

                entry<Destination.StreamerEditProfile> {
                    val editProfileViewModel: EditProfileViewModel = hiltViewModel()

                    var currentSubScreen by rememberSaveable { mutableStateOf("profile") }

                    BackHandler(enabled = currentSubScreen == "tags") {
                        currentSubScreen = "profile"
                    }

                    AnimatedContent(
                        targetState = currentSubScreen,
                        label = "EditFlow"
                    ) { subScreen ->
                        when (subScreen) {
                            "profile" -> {
                                EditProfileScreen(
                                    viewModel = editProfileViewModel,
                                    modifier = Modifier.fillMaxSize(),
                                    onNavUp = { backStack.removeLastOrNull() },
                                    onEditTagsNav = { currentSubScreen = "tags" }
                                )
                            }
                            "tags" -> {
                                SelectTagsScreen(
                                    viewModel = editProfileViewModel,
                                    modifier = Modifier.fillMaxSize(),
                                    onNavUp = { currentSubScreen = "profile" }
                                )
                            }
                        }
                    }
                }

                entry<Destination.Search> {
                    SearchScreen(
                        modifier = Modifier.fillMaxSize(),
                        onStreamerClick = { id ->
                            backStack.add(Destination.StreamerDetail(streamerId = id))
                        }
                    )
                }
            }
        )
    }
}