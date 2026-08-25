package com.strimup.feature.home.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.strimup.core.navigation.Destination2
import com.strimup.feature.home.presentation.HomeScreen
import com.strimup.feature.home.presentation.HomeViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun HomeNavigation(
    onStreamerClick: (String) -> Unit,
    onStreamerBannerClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Destination2.Home.StreamerList::class, Destination2.Home.StreamerList.serializer())
                }
            }
        },
        Destination2.Home.StreamerList
    )

    NavDisplay(
        backStack = homeBackStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Destination2.Home.StreamerList> {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    onStreamerClick = onStreamerClick,
                    onStreamerBannerClick = onStreamerBannerClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}