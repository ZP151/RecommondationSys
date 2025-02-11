package com.example.recommendationsys.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecommendRetrofitInstance {
    private const val BASE_URLofRestaurant = "http://13.212.250.206:8000/"

    val api: RecommendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URLofRestaurant)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecommendApiService::class.java)
    }
}
