package com.strimup.common.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {

    @Serializable
    object Home : Destination

    @Serializable
    data class Profile(val streamerId: String) : Destination
}