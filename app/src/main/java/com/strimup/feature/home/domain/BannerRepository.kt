package com.strimup.feature.home.domain

interface BannerRepository {
    suspend fun getBannerItems()
}