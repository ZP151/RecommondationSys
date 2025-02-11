package com.example.recommendationsys.data.network

import com.example.recommondationsys.data.model.UserPreference
import com.example.recommondationsys.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UserPreferenceApiService {
    @POST("/api/preferences/{userId}")
    suspend fun savePreferences(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body preference: UserPreference
    ): UserPreference

    @GET("/api/preferences/{userId}")
    suspend fun getPreferences(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): UserPreference
}

object UserPrefManager {
    private val apiService: UserPreferenceApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(UserPreferenceApiService::class.java)
    }

    suspend fun saveUserPreference(userId: String, preference: UserPreference) {
        val token = UserManager.getToken() ?: return
        apiService.savePreferences("Bearer $token", userId, preference)
    }

    suspend fun getUserPreference(userId: String): UserPreference? {
        val token = UserManager.getToken() ?: return null
        return apiService.getPreferences("Bearer $token", userId)
    }
}
