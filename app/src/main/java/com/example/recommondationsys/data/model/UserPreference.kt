package com.example.recommondationsys.data.model



data class UserPreference(
    val id: String,
    val userId: String,  // Associated User ID
    var dietPreference: String = "No dietary restrictions",  // 🍽 Dietary preference
    var preferredCuisines: List<String> = emptyList(),  // 🍱 Preferred cuisines
    var pricePreference: String = "$ affordable",  // 💰 Price preference


    //不用了
    /*var transportMode: String = "Walking",  // 🚶 Mode of transportation
    var diningTime: String = "Fast food",  // ⏳ Dining duration
    var restaurantType: String = "🏢 Chain restaurant"  // 🏬 Restaurant type*/

)
