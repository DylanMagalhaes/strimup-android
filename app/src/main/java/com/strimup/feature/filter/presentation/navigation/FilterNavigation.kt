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
import com.strimup.common.navigation.Destination2
import com.strimup.feature.filter.presentation.create.CreateFilterScreen
import com.strimup.feature.filter.presentation.create.CreateFilterViewModel
import com.strimup.feature.filter.presentation.create.SelectFilterTagsScreen
import com.strimup.feature.filter.presentation.list.FilterListScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun FilterNavigation(
    modifier: Modifier = Modifier
) {
    val filterBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Destination2.Filter.List::class, Destination2.Filter.List.serializer())
                    subclass(Destination2.Filter.Create::class, Destination2.Filter.Create.serializer())
                    subclass(Destination2.Filter.SelectTags::class, Destination2.Filter.SelectTags.serializer())
                }
            }
        },
        Destination2.Filter.List
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
            entry<Destination2.Filter.List> {
                FilterListScreen(
                    modifier = Modifier.fillMaxSize(),
                    onCreateFilterClick = {
                        filterBackStack.add(Destination2.Filter.Create)
                    }
                )
            }

            entry<Destination2.Filter.Create> {
                CreateFilterScreen(
                    viewModel = createFilterViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { filterBackStack.removeLastOrNull() },
                    onEditTagNav = {
                        filterBackStack.add(Destination2.Filter.SelectTags)
                    }
                )
            }

            entry<Destination2.Filter.SelectTags> {
                SelectFilterTagsScreen(
                    viewModel = createFilterViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { filterBackStack.removeLastOrNull() }
                )
            }
        }
    )
}