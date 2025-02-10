package com.example.recommondationsys.data.model


data class User(
    val id: String,
    val username: String,
    val password: String,
    val age: Int? = null,
    var isNewUser: Boolean = true, // 迁移 isNewUser 到 User 里

)
