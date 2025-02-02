package com.example.recommondationsys.data

import com.example.recommondationsys.data.entity.User
import com.example.recommondationsys.data.entity.UserPreference


object FakeDatabase {
    private val users = mutableListOf<User>(
        User(
            id = "admin-id",
            username = "admin",
            password = "123",
            isNewUser = false
        )
    )
    private val userPreferences = mutableListOf<UserPreference>()

    fun getUserById(userId: String): User? {
        return users.find { it.id == userId }
    }

    fun getUserByUsername(username: String): User? {
        return users.find { it.username == username }
    }

    fun addUser(user: User) {
        users.add(user)
        /*if (users.any { it.username == user.username }) {
            println("DEBUG: 用户已存在，未添加 -> ${user.username}")
            return
        }
        users.add(user)
        println("DEBUG: 新用户已添加 -> ${user.username}")*/
    }

    fun updateUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
        }
    }
    fun getUserPreferenceByUserId(userId: String): UserPreference? {
        return userPreferences.find { it.userId == userId }
    }

    fun saveUserPreference(userPreference: UserPreference) {
        val index = userPreferences.indexOfFirst { it.userId == userPreference.userId }
        if (index != -1) {
            userPreferences[index] = userPreference
        } else {
            userPreferences.add(userPreference)
        }
    }
}
