package com.strimup.feature.cuts.data

import com.strimup.feature.cuts.data.response.CutLikeResponse
import com.strimup.feature.cuts.data.response.CutsResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CutsApiService {
    @GET("api/cuts")
    suspend fun getCuts(
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String?,
    ): CutsResponse

    @POST("api/cuts/{videoId}/like")
    suspend fun like(@Path("videoId") videoId: String): CutLikeResponse

    @DELETE("api/cuts/{videoId}/like")
    suspend fun unlike(@Path("videoId") videoId: String): CutLikeResponse
}
