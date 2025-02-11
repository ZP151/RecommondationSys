package com.example.recommendationsys.data.network

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"  // 你的后端地址

    private val cookieManager = CookieManager()

    val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager)) // ✅ 让 Retrofit 自动存储和携带 Cookie
        .build()

    val api: RestaurantApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // ✅ 绑定 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestaurantApiService::class.java)
    }
}
