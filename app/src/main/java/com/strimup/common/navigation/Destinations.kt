package com.strimup.common.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {

    @Serializable
    data object Home : Destination

    @Serializable
    data class StreamerDetail(val streamerId: String) : Destination

    @Serializable
    data class StreamerProfile(val streamerId: String?) : Destination

    @Serializable
    data object StreamerEditProfile : Destination

    @Serializable
    data object FilterList : Destination

    @Serializable
    data object CreateFilter : Destination

    @Serializable
    data object CreateFilterEditTag : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object Search : Destination

    @Serializable
    data object StreamerEditTags : Destination
}