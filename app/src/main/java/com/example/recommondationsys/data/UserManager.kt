package com.example.recommondationsys.data

import java.util.UUID

object UserManager {
    private val users = mutableListOf<User>(
        User(
            id = UUID.randomUUID().toString(),  // 预设唯一 ID
            username = "admin",
            password = "123",
            isNewUser = false // 设定 admin 不是新用户
        )
    )

    fun registerUser(username: String, password: String, age: Int? = null): Boolean {
        if (users.any { it.username == username }) {
            return false // 用户已存在
        }
        val newUser = User(
            id = UUID.randomUUID().toString(),
            username = username,
            password = password,
            age = age,
            isNewUser = true // 新注册用户
        )
        users.add(newUser)
        return true
    }

    fun validateUser(username: String, password: String): Boolean {
        return users.any { it.username == username && it.password == password }
    }

    fun getUserByUsername(username: String): User? {
        return users.find { it.username == username }
    }

    fun setUserNotNew(username: String) {
        users.find { it.username == username }?.isNewUser = false
    }

    fun isNewUser(username: String): Boolean {
        return users.find { it.username == username }?.isNewUser ?: true
    }
}
