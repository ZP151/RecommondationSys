package com.example.recommendationsys.data.remote

import com.example.recommendationsys.data.model.Restaurant
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApiService {

    // 获取推荐餐厅（由后端提供，不再调用 Google API）
//    @GET("restaurants/recommendations")
//    suspend fun getRecommendedRestaurants(
//        @Query("userId") userId: String
//    ): Response<List<Restaurant>>
    @GET("restaurants/recommendations")
    suspend fun getRecommendations(): Response<List<Restaurant>>

    // 获取用户收藏的餐厅
    @GET("favorites")
    suspend fun getFavorites(@Query("userId") userId: String): Response<List<Restaurant>>

    // 添加餐厅到收藏
    @POST("favorites/add")
    suspend fun addFavorite(
        @Query("userId") userId: String,
        @Body restaurant: Restaurant
    ): Response<Void>

    // 从收藏中移除餐厅
    @DELETE("favorites/remove/{restaurantId}")
    suspend fun removeFavorite(
        @Query("userId") userId: String,
        @Path("restaurantId") restaurantId: String
    ): Response<Void>
}
