package com.example.recommendationsys.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recommendationsys.data.model.Restaurant
import com.example.recommendationsys.data.remote.RestaurantApiService
import kotlinx.coroutines.launch

class RecommendationViewModel(private val apiService: RestaurantApiService) : ViewModel() {

    private val _restaurantList = MutableLiveData<List<Restaurant>>()
    val recommendations: LiveData<List<Restaurant>> get() = _restaurantList

    fun fetchRecommendations() {
        viewModelScope.launch {
            val response = apiService.getRecommendations()
            if (response.isSuccessful) {
                _restaurantList.postValue(response.body())
            }
        }
    }
}
