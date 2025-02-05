package com.example.recommendationsys.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _chatMessages = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf("Test Message 1", "Test Message 2", "Test Message 3")
    }
    val chatMessages: LiveData<MutableList<String>> = _chatMessages

    private val _favourites = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }
    val favourites: LiveData<MutableList<String>> = _favourites

    fun addMessage(message: String) {
        val updatedMessages = _chatMessages.value?.toMutableList() ?: mutableListOf()
        updatedMessages.add(message)
        _chatMessages.value = updatedMessages // 通知观察者更新数据
    }



    fun addFavourite(message: String) {
        if (message.startsWith("Sys: recommend restaurant based on your input ->")) {
            val item = message.replace("Sys: ", "")
            if (_favourites.value?.contains(item) == false) {
                _favourites.value?.apply {
                    add(item)
                    _favourites.value = this
                }
            }
        }
    }
}
