package com.example.recommendationsys.data.network

import com.example.recommondationsys.data.model.Restaurant
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApiService {

    // 1⃣ 获取推荐的餐厅,后面测试没问题就用
    @GET("restaurants/recommend")
    suspend fun getRecommendations(@Query("query") query: String): Response<Map<String, Any>>

    // 2⃣ 获取收藏的餐厅
    @GET("restaurants/favorites/{userId}")
    suspend fun getFavoriteRestaurants(@Path("userId") userId: String): Response<List<Restaurant>>

    // 收藏餐厅（传递整个餐厅对象）
    @POST("restaurants/favorite/{userId}")
    suspend fun addFavorite(
        @Path("userId") userId: String,
        @Body restaurant: Restaurant
    ): Response<Void>

    // 取消收藏
    @DELETE("favorite/{userId}/{restaurantId}")
    suspend fun removeFavorite(
        @Path("userId") userId: String,
        @Path("restaurantId") restaurantId: String
    ): Response<Void>

}
