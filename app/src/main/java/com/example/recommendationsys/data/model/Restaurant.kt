package com.example.recommendationsys.data.model

data class Restaurant(
    val name: String,
    val address: String,
    val rating: Double,
    val userRatingsTotal: Int,
    val types: List<String>,
    val placeId: String,  // 作为唯一标识符
    val priceLevel: Int?,
    val phoneNumber: String?,
    val website: String?,
    val photoReference: String?,
    val latitude: Double,
    val longitude: Double,
    val predictedScore: Double,
    val penalty: Double,
    val adjustedScore: Double
)