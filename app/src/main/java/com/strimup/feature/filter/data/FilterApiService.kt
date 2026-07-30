package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.response.FilterResponse
import retrofit2.http.GET

interface FilterApiService {
    @GET("api/filters")
    suspend fun getFilters(): List<FilterResponse>
}