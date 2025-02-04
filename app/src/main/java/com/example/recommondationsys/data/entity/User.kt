package com.example.recommondationsys.data.entity


data class User(
    val id: String,
    val username: String,
    val password: String,
    val age: Int? = null,
    var isNewUser: Boolean = true, // 迁移 isNewUser 到 User 里

   /* val preferences: List<String>? = null,
    var preferenceId: String? = null // 关联 UserPreference 的 ID*/
)
