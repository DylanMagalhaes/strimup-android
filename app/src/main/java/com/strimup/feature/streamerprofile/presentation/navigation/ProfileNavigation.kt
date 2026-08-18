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
import com.strimup.common.navigation.Destination2
import com.strimup.feature.streamerprofile.presentation.editeprofile.EditProfileScreen
import com.strimup.feature.streamerprofile.presentation.editeprofile.EditProfileViewModel
import com.strimup.feature.streamerprofile.presentation.editeprofile.SelectProfileTagsScreen
import com.strimup.feature.streamerprofile.presentation.streamerprofile.StreamerProfileScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun ProfileNavigation(
    userId: String?,
    modifier: Modifier = Modifier
) {
    val initialDestination = Destination2.Profile.View(userId = userId)

    val profileBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Destination2.Profile.View::class, Destination2.Profile.View.serializer())
                    subclass(Destination2.Profile.Edit::class, Destination2.Profile.Edit.serializer())
                    subclass(Destination2.Profile.EditTags::class, Destination2.Profile.EditTags.serializer())
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
            entry<Destination2.Profile.View> {
                StreamerProfileScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { profileBackStack.removeLastOrNull() },
                    onEditProfileNav = {
                        profileBackStack.add(Destination2.Profile.Edit)
                    }
                )
            }

            entry<Destination2.Profile.Edit> {
                EditProfileScreen(
                    viewModel = editProfileViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { profileBackStack.removeLastOrNull() },
                    onEditTagsNav = {
                        profileBackStack.add(Destination2.Profile.EditTags)
                    }
                )
            }

            entry<Destination2.Profile.EditTags> {
                SelectProfileTagsScreen(
                    viewModel = editProfileViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavUp = { profileBackStack.removeLastOrNull() }
                )
            }
        }
    )
}