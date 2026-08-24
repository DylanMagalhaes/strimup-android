package com.strimup.feature.home.data

import retrofit2.http.GET

interface BannerApiService {
    @GET("api/banners")
    fun getBannerItems(): BannerResponse
}