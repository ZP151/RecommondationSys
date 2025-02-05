package com.example.recommendationsys.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recommendationsys.data.model.Restaurant
import com.example.recommendationsys.data.remote.RestaurantApiService
import kotlinx.coroutines.launch


class FavoriteViewModel(private val apiService: RestaurantApiService) : ViewModel() {

    private val _favorites = MutableLiveData<List<Restaurant>>()
    val favorites: LiveData<List<Restaurant>> get() = _favorites


    // 获取用户收藏的餐厅
    fun fetchFavorites(userId: String) {
        viewModelScope.launch {
            val response = apiService.getFavorites(userId)
            if (response.isSuccessful) {
                _favorites.postValue(response.body() ?: emptyList())  // 确保为空时不会出错
            } else {
                _favorites.postValue(emptyList())  // 请求失败时返回空列表
            }
        }
    }

    fun toggleFavorite(userId: String, restaurant: Restaurant) { // ✅ 传入 userId
        viewModelScope.launch {
            if (favorites.value?.any { it.placeId == restaurant.placeId } == true) {
                apiService.removeFavorite(userId, restaurant.placeId)
            } else {
                apiService.addFavorite(userId, restaurant)
            }
            fetchFavorites(userId) // ✅ 更新收藏列表时也要传 userId
        }
    }
}
