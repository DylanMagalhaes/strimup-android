package com.strimup.feature.favorite.data


import com.strimup.feature.favorite.data.response.FavoriteStreamerResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApiService {
    @GET("api/favorites")
    suspend fun getFavoriteStreamers(): List<FavoriteStreamerResponse>

    @POST("api/favorites/{id}")
    suspend fun addFavoriteStreamer(
        @Path("id") id: String
    )

    @DELETE("api/favorites/{id}")
    suspend fun deleteFavoriteStreamer(
        @Path("id") id: String
    )
}