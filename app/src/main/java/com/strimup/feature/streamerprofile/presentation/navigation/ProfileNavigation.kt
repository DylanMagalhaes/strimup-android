package com.strimup.feature.streamerprofile.presentation.navigation

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
import com.strimup.feature.streamerprofile.presentation.editprofile.EditProfileScreen
import com.strimup.feature.streamerprofile.presentation.editprofile.EditProfileViewModel
import com.strimup.feature.streamerprofile.presentation.editprofile.SelectProfileTagsScreen
import com.strimup.feature.streamerprofile.presentation.streamerprofile.StreamerProfileScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun ProfileNavigation(
    userId: String?,
    modifier: Modifier = Modifier
) {
    val initialDestination = Destination.Profile.View(userId = userId)

    val profileBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Destination.Profile.View::class, Destination.Profile.View.serializer())
                    subclass(Destination.Profile.Edit::class, Destination.Profile.Edit.serializer())
                    subclass(Destination.Profile.EditTags::class, Destination.Profile.EditTags.serializer())
                }
            }
        },
        initialDestination
    )

    val editProfileViewModel: EditProfileViewModel = hiltViewModel()

    NavDisplay(
        backStack = profileBackStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Destination.Profile.View> {
                StreamerProfileScreen(
                    modifier = Modifier.fillMaxSize(),
                    onEditProfileNav = {
                        profileBackStack.add(Destination.Profile.Edit)
                    },
                )
            }

            entry<Destination.Profile.Edit> {
                EditProfileScreen(
                    viewModel = editProfileViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { profileBackStack.removeLastOrNull() },
                    onEditTagsNav = {
                        profileBackStack.add(Destination.Profile.EditTags)
                    }
                )
            }

            entry<Destination.Profile.EditTags> {
                SelectProfileTagsScreen(
                    viewModel = editProfileViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { profileBackStack.removeLastOrNull() }
                )
            }
        }
    )
}