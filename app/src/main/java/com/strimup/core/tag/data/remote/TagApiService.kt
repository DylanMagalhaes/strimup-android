package com.strimup.core.tag.data.remote

import com.strimup.core.tag.data.response.TagResponse
import retrofit2.http.GET

interface TagApiService {
    @GET("api/tag")
    suspend fun getTags(): List<TagResponse>
}