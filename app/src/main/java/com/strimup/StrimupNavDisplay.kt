package com.strimup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.strimup.common.navigation.Destination
import com.strimup.feature.auth.presentation.login.LoginScreen
import com.strimup.feature.home.presentation.HomeScreen
import com.strimup.feature.streamerdetail.presentation.StreamerDetailScreen

@Composable
fun StrimupNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Destination.Login)

    NavDisplay(
        modifier = modifier,
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
        }
    )
}
