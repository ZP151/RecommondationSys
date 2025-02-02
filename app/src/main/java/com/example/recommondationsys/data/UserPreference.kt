package com.example.recommondationsys.data



data class UserPreference(
    val id: String,
    val userId: String,  // 关联 User 的 ID
    var dietPreference: String = "无忌口",  // 🍽 饮食习惯
    var preferredCuisines: List<String> = emptyList(),  // 🍱 偏好菜系
    var transportMode: String = "步行",  // 🚶 出行方式
    var pricePreference: String = "💲 实惠",  // 💰 价格偏好
    var diningTime: String = "快餐",  // ⏳ 就餐时长
    var restaurantType: String = "🏢 连锁店",  // 🏬 餐厅类型
)
