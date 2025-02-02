package com.example.recommondationsys.data

import java.util.UUID

object UserPrefManager {
    private val userPreferences = mutableListOf<UserPreference>()

    // 关联 User 的 ID，而不是 username
    fun saveUserPreference(userPreference: UserPreference) {
        val existingPrefIndex = userPreferences.indexOfFirst { it.userId == userPreference.userId }
        if (existingPrefIndex != -1) {
            userPreferences[existingPrefIndex] = userPreference  // 更新
        } else {
            userPreferences.add(userPreference)  // 新增
        }
    }

    fun getUserPreference(userId: String): UserPreference {
        return userPreferences.find { it.userId == userId }
            ?: UserPreference(id = UUID.randomUUID().toString(), userId = userId).also { userPreferences.add(it) }
    }
    /*fun getUserPreference(userId: String): UserPreference? {
        return userPreferences.find { it.userId == userId }
    }*/

}
