package com.example.recommendationsys.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendApiService {
    @GET("recommend")
    suspend fun getRecommendations(@Query("query") query: String): Response<Map<String, Any>>
}