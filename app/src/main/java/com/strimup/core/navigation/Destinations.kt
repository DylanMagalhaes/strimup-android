package com.strimup.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    sealed interface Home : Destination {
        @Serializable
        data object StreamerList : Home
    }

    @Serializable
    sealed interface Filter : Destination {
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
    data object Search : Destination

    @Serializable
    data object Favorite : Destination

    @Serializable
    sealed interface Profile : Destination {
        @Serializable
        data class View(val userId: String?) : Profile

        @Serializable
        data object Edit : Profile

        @Serializable
        data object EditTags : Profile
    }

    @Serializable
    data class StreamerDetail(val streamerId: String) : Destination

    @Serializable
    data class YouTubePlayer(
        val videoId: String,
        val isVertical: Boolean = false,
    ) : Destination

    @Serializable
    data object Login : Destination
}