package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.request.CreateFilterRequest
import com.strimup.feature.filter.data.response.FilterResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FilterApiService {
    @GET("api/filters")
    suspend fun getFilters(): List<FilterResponse>

    @POST("api/filter")
    suspend fun createFilter(@Body request: CreateFilterRequest): FilterResponse

    @DELETE("api/filters/{id}")
    suspend fun deleteFilterById(
        @Path("id") id: String
    )
}