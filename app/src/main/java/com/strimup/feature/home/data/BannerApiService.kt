package com.strimup.feature.home.data

import com.strimup.feature.home.data.response.BannerItemsResponse
import retrofit2.http.GET

interface BannerApiService {
    @GET("api/banners")
    suspend fun getBannerItems(): List<BannerItemsResponse>
}