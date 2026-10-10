package com.strimup.feature.filter.presentation.navigation

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
import com.strimup.core.navigation.Destination
import com.strimup.feature.filter.presentation.create.CreateFilterScreen
import com.strimup.feature.filter.presentation.create.CreateFilterViewModel
import com.strimup.feature.filter.presentation.create.SelectFilterTagsScreen
import com.strimup.feature.filter.presentation.list.FilterListScreen
import com.strimup.feature.filter.presentation.matchedstreamer.MatchedStreamersScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun FilterNavigation(
    onStreamerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Destination.Filter.List::class, Destination.Filter.List.serializer())
                    subclass(
                        Destination.Filter.Create::class,
                        Destination.Filter.Create.serializer()
                    )
                    subclass(
                        Destination.Filter.SelectTags::class,
                        Destination.Filter.SelectTags.serializer()
                    )
                    subclass(
                        Destination.Filter.Result::class,
                        Destination.Filter.Result.serializer()
                    )
                }
            }
        },
        Destination.Filter.List
    )

    val createFilterViewModel: CreateFilterViewModel = hiltViewModel()

    NavDisplay(
        backStack = filterBackStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Destination.Filter.List> {
                FilterListScreen(
                    modifier = Modifier.fillMaxSize(),
                    onCreateFilterClick = {
                        filterBackStack.add(Destination.Filter.Create)
                    },
                    onFilterClick = { filterId ->
                        filterBackStack.add(Destination.Filter.Result(filterId))
                    }
                )
            }

            entry<Destination.Filter.Result> { destination ->
                MatchedStreamersScreen(
                    onNavUp = { filterBackStack.removeLastOrNull() },
                    onStreamerClick = onStreamerClick,
                    filterId = destination.filterId,
                )
            }

            entry<Destination.Filter.Create> {
                CreateFilterScreen(
                    viewModel = createFilterViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { filterBackStack.removeLastOrNull() },
                    onEditTagNav = {
                        filterBackStack.add(Destination.Filter.SelectTags)
                    }
                )
            }

            entry<Destination.Filter.SelectTags> {
                SelectFilterTagsScreen(
                    viewModel = createFilterViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { filterBackStack.removeLastOrNull() }
                )
            }
        }
    )
}