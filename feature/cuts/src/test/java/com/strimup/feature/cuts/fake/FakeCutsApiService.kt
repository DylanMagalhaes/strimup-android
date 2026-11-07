package com.strimup.feature.cuts.fake

import com.strimup.feature.cuts.data.CutsApiService
import com.strimup.feature.cuts.data.response.CutLikeResponse
import com.strimup.feature.cuts.data.response.CutsResponse

class FakeCutsApiService(
    var cutsResponse: () -> CutsResponse = { CutsResponse() },
    var likeResponse: () -> CutLikeResponse = { CutLikeResponse(liked = true, likesCount = 1) },
) : CutsApiService {
    val requestedCursors = mutableListOf<String?>()
    val likeCalls = mutableListOf<Pair<String, Boolean>>()

    override suspend fun getCuts(limit: Int, cursor: String?): CutsResponse {
        requestedCursors += cursor
        return cutsResponse()
    }

    override suspend fun like(videoId: String): CutLikeResponse {
        likeCalls += videoId to true
        return likeResponse()
    }

    override suspend fun unlike(videoId: String): CutLikeResponse {
        likeCalls += videoId to false
        return likeResponse()
    }
}
