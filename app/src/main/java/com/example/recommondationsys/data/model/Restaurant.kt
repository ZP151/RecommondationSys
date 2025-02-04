package com.example.recommondationsys.data.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class Restaurant(
    val id: String,  // Google API 提供的 Place ID 作为主键
    val name: String,
    val address: String,
    val rating: Float?,
    val imageUrl: String?,
    val isFavorite: Boolean = false  // 方便 UI 直接显示收藏状态
) : Parcelable {
    companion object {
        fun fromJson(json: JSONObject): Restaurant {
            return Restaurant(
                id = json.optString("place_id"),
                name = json.optString("name"),
                address = json.optString("vicinity"),
                rating = json.optDouble("rating", 0.0).toFloat(),
                imageUrl = json.optJSONArray("photos")?.getJSONObject(0)?.optString("photo_reference")
            )
        }
    }
}