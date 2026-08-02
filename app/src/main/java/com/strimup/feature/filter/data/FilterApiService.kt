package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.response.FilterResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface FilterApiService {
    @GET("api/filters")
    suspend fun getFilters(): List<FilterResponse>

    @DELETE("api/filters/{id}")
    suspend fun deleteFilterById(
        @Path("id") id: String
    )
}