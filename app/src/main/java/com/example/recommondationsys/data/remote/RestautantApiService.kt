package com.example.recommondationsys.data.remote


import com.example.recommondationsys.data.model.Restaurant
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApiService {

    // 1⃣ 从 Google API 获取推荐的餐厅列表
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getRecommendedRestaurants(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "restaurant",
        @Query("key") apiKey: String
    ): Response<JSONObject>

    // 2⃣ 添加收藏（发送给后端）
    @POST("favorites")
    suspend fun addFavorite(@Body restaurant: Restaurant): Response<Void>

    // 3⃣ 获取收藏的餐厅列表（从后端获取）
    @GET("favorites")
    suspend fun getFavorites(): List<Restaurant>

    // 4⃣ 取消收藏
    @DELETE("favorites/{id}")
    suspend fun removeFavorite(@Path("id") restaurantId: String): Response<Void>
}