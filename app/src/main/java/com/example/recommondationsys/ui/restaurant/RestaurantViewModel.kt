package com.example.recommondationsys.ui.restaurant


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recommendationsys.data.network.RetrofitInstance
import com.example.recommondationsys.data.model.ChatMessage
import com.example.recommondationsys.data.model.MessageType
import com.example.recommondationsys.data.model.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantViewModel : ViewModel() {
    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> get() = _restaurants

    private val _favoriteRestaurants = MutableLiveData<List<Restaurant>>()
    val favoriteRestaurants: LiveData<List<Restaurant>> get() = _favoriteRestaurants

    private val _chatMessages = MutableLiveData<List<ChatMessage>>(emptyList()) // 存储消息
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun addChatMessage(message: ChatMessage) {
        _chatMessages.value = (_chatMessages.value ?: emptyList()) + message
    }

    /**
     * 发送查询并获取推荐的餐厅
     */
    fun fetchRecommendedRestaurants(query: String) {
        //repeated userchat
//        val userMessage = ChatMessage(query, MessageType.USER)
//        _chatMessages.value = _chatMessages.value.orEmpty() + userMessage

        viewModelScope.launch(Dispatchers.IO) {
            // 正在加载
            _isLoading.postValue(true)
            val response = RetrofitInstance.api.getRecommendations(query)

            if (response.isSuccessful) {

                val responseBody = response.body()
                val success = responseBody?.get("success") as? Boolean ?: false
                val message = responseBody?.get("message") as? String ?:"无可用信息"

                if (!success) {
                    //此时取用message的值，并加到chatmessage里
                    // 2️⃣ 添加系统消息
                    val systemMessage = ChatMessage(message, MessageType.SYSTEM)
                    withContext(Dispatchers.Main) {
                        _chatMessages.value = (_chatMessages.value ?: emptyList()) + systemMessage
                    }
                } else {
                    //此时success成功，应该处理返回的restaurant list
                    val restaurantsData = responseBody?.get("restaurants") as? List<Map<String, Any>> ?: emptyList()

                    val restaurantList = restaurantsData.map { json ->
                        Restaurant(
                            placeId = json["place_id"] as? String ?: "",
                            name = json["name"] as? String ?: "",
                            address = json["address"] as? String ?: "",
                            rating = (json["rating"] as? Number)?.toDouble() ?: 0.0,
                            userRatingsTotal = (json["user_ratings_total"] as? Number)?.toInt() ?: 0,
                            priceLevel = (json["price_level"] as? Number)?.toInt() ?: -1,
                            phoneNumber = json["phone"] as? String ?: "No data available",
                            website = json["website"] as? String ?: "No data available",
                            latitude = (json["location"] as? Map<String, Number>)?.get("lat")?.toDouble() ?: 0.0,
                            longitude = (json["location"] as? Map<String, Number>)?.get("lng")?.toDouble() ?: 0.0,
                            photoReference = (json["photos"] as? List<String>)?.firstOrNull() ?: "",
                            isFavorite = false  // 先默认是未收藏
                        )
                    }

                    // 检查每个餐厅是否被收藏
                    val updatedRestaurants = restaurantList.map { restaurant ->
                        restaurant.copy(isFavorite = isFavorite(restaurant.placeId))
                    }

                    val recommendationMessage = ChatMessage(
                        text = "推荐的餐厅如下：",
                        type = MessageType.RECOMMENDATION,
                        recommendations = updatedRestaurants
                    )
                    withContext(Dispatchers.Main) {
                        // 加载完成切换加载状态
                        _isLoading.value = false

                        _chatMessages.value = _chatMessages.value.orEmpty() + recommendationMessage
                    }
                }
            } else {
                // 加载完成切换加载状态
                _isLoading.value = false
            }
        }
        /*viewModelScope.launch(Dispatchers.IO) {
            val response = RecommendRetrofitInstance.api.getRecommendations(query)

            if (response.isSuccessful) {
                val responseBody = response.body()
                val success = responseBody?.get("success") as? Boolean ?: false
                val message = responseBody?.get("message") as? String ?: "No message"

                val updatedChatMessages = mutableListOf<ChatMessage>()

                // 1️⃣ 先添加用户输入的消息
                updatedChatMessages.add(ChatMessage(query, MessageType.USER))

                if (!success) {
                    // 2️⃣ 添加系统消息（系统返回错误）
                    updatedChatMessages.add(ChatMessage(message, MessageType.SYSTEM))
                } else {
                    // 3️⃣ 添加系统消息（正常的系统回复）
                    updatedChatMessages.add(ChatMessage(message, MessageType.SYSTEM))

                    // 解析餐厅数据
                    val restaurantsData = responseBody?.get("restaurants") as? List<Map<String, Any>> ?: emptyList()
                    val restaurantList = restaurantsData.map { json ->
                        Restaurant(
                            placeId = json["place_id"] as? String ?: "",
                            name = json["name"] as? String ?: "",
                            address = json["address"] as? String ?: "",
                            rating = (json["rating"] as? Number)?.toDouble() ?: 0.0,
                            userRatingsTotal = (json["user_ratings_total"] as? Number)?.toInt() ?: 0,
                            priceLevel = (json["price_level"] as? Number)?.toInt() ?: -1,
                            phoneNumber = json["phone"] as? String ?: "No data available",
                            website = json["website"] as? String ?: "No data available",
                            latitude = (json["location"] as? Map<String, Number>)?.get("lat")?.toDouble() ?: 0.0,
                            longitude = (json["location"] as? Map<String, Number>)?.get("lng")?.toDouble() ?: 0.0,
                            photoReference = (json["photos"] as? List<String>)?.firstOrNull() ?: ""
                        )
                    }

                    if (restaurantList.isNotEmpty()) {
                        updatedChatMessages.add(
                            ChatMessage(
                                text = "推荐的餐厅如下：",
                                type = MessageType.RECOMMENDATION,
                                recommendations = restaurantList
                            )
                        )
                    } else {
                        updatedChatMessages.add(ChatMessage("没有找到相关的餐厅。", MessageType.SYSTEM))
                    }
                }

                withContext(Dispatchers.Main) {
                    _chatMessages.value = (_chatMessages.value ?: emptyList()) + updatedChatMessages
                }
            }
        }*/

    }

/*
    //这个获取收藏列表，获取推荐列表的餐厅要在同一个组件里加载吗，还是不同的？？
*/
    /**
     * 获取用户收藏的餐厅
     */
    fun loadFavoriteRestaurants(userId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            // 正在加载
            _isLoading.postValue(true)

            val response = RetrofitInstance.api.getFavoriteRestaurants(userId)
            if (response.isSuccessful) {
                val favorites = response.body() ?: emptyList()
                val favoriteIds = favorites.map { it.placeId }

                val parsedRestaurants = favorites.map { restaurant ->
                    Restaurant(
                        placeId = restaurant.placeId,
                        name = restaurant.name,
                        address = restaurant.address,
                        rating = restaurant.rating,
                        userRatingsTotal = restaurant.userRatingsTotal,
                        priceLevel = restaurant.priceLevel,
                        phoneNumber = restaurant.phoneNumber,
                        website = restaurant.website,
                        latitude = restaurant.latitude,
                        longitude = restaurant.longitude,
                        photoReference = restaurant.photoReference,
                        isFavorite = true // ✅ 这里直接标记为收藏
                    )
                }
                // 生成收藏餐厅的消息，并加入 `chatMessages`
                val favoriteMessage = ChatMessage(
                    text = "您的收藏餐厅如下：",
                    type = MessageType.RECOMMENDATION,
                    recommendations = parsedRestaurants
                )
                withContext(Dispatchers.Main) {
                    // 加载完成切换加载状态
                    _isLoading.value = false
                    _favoriteRestaurants.value = favorites
                    _chatMessages.value = _chatMessages.value.orEmpty() + favoriteMessage
                }
            } else {
                // 加载完成切换加载状态
                _isLoading.postValue(false)
            }
        }
    }


    /**
     * 切换收藏状态，【点击收藏餐厅 / 取消收藏】
     */
    fun toggleFavorite(userId: String, restaurant: Restaurant) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = if (restaurant.isFavorite) {
                RetrofitInstance.api.removeFavorite(userId, restaurant.placeId)
            } else {
                RetrofitInstance.api.addFavorite(userId, restaurant)
            }

            if (response.isSuccessful) {
                // 重新获取最新的收藏列表
                val favoritesResponse = RetrofitInstance.api.getFavoriteRestaurants(userId)

                if (favoritesResponse.isSuccessful) {
                    val updatedFavorites = favoritesResponse.body() ?: emptyList()

                    withContext(Dispatchers.Main) {
                        _favoriteRestaurants.value = updatedFavorites

                        // 更新 `_restaurants` 列表中的 `isFavorite` 状态
                        _restaurants.value = _restaurants.value?.map { r ->
                            r.copy(isFavorite = updatedFavorites.any { it.placeId == r.placeId })
                        }
                        // 追加收藏餐厅的消息
                        val favoriteMessage = ChatMessage(
                            text = "您的收藏餐厅如下：",
                            type = MessageType.RECOMMENDATION,
                            recommendations = updatedFavorites
                        )
                        _chatMessages.value = _chatMessages.value.orEmpty() + favoriteMessage
                    }
                }
            }
        }
    }

    // 调用接口检查餐厅是否被收藏
    private suspend fun isFavorite(restaurantId: String): Boolean {
        return try {
            val response = RetrofitInstance.api.matchFavoriteRestaurant(restaurantId)
            response.isSuccessful && response.body() == true
        } catch (e: Exception) {
            false
        }
    }

}
