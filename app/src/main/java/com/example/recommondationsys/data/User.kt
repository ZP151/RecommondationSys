package com.example.recommondationsys.data

data class User(
    val username: String,
    val password: String,
    val age: Int? = null,
    val preferences: List<String>? = null
)
