package com.strimup.core.navigation

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

@Serializable
sealed interface Destination2 : NavKey {
    @Serializable
    sealed interface Home : Destination2 {
        @Serializable
        data object StreamerList : Home
    }

    @Serializable
    sealed interface Filter : Destination2 {
        @Serializable
        data object List : Filter

        @Serializable
        data class Result(val filterId: String) : Filter

        @Serializable
        data object Create : Filter

        @Serializable
        data object SelectTags : Filter
    }

    @Serializable
    data object Search : Destination2

    @Serializable
    sealed interface Profile : Destination2 {
        @Serializable
        data class View(val userId: String?) : Profile

        @Serializable
        data object Edit : Profile

        @Serializable
        data object EditTags : Profile
    }

    @Serializable
    data class StreamerDetail(val streamerId: String) : Destination2

    @Serializable
    data class YouTubePlayer(
        val videoId: String,
        val isVertical: Boolean = false,
    ) : Destination

    @Serializable
    data object Login : Destination2
}