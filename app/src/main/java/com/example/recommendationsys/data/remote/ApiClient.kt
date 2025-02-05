package com.example.recommendationsys.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://your-backend-url.com/api/" // 替换成你的后端 API 地址

    // 通用 Retrofit 实例
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 提供 RestaurantApiService 实例
    val restaurantApiService: RestaurantApiService by lazy {
        retrofit.create(RestaurantApiService::class.java)
    }

    // 提供 UserApiService 实例
//    val userApiService: UserApiService by lazy {
//        retrofit.create(UserApiService::class.java)
//    }

    // 如果未来有更多 API Service，可以继续添加
}

