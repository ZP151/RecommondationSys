package com.example.recommondationsys.data


data class User(
    val id: String,
    val username: String,
    val password: String,
    val age: Int? = null,
    val preferences: List<String>? = null,
    var isNewUser: Boolean = true, // 迁移 isNewUser 到 User 里
    var preferenceId: String? = null // 关联 UserPreference 的 ID
)
