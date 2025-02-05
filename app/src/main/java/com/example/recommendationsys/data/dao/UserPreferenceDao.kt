package com.example.recommendationsys.data.dao


import com.example.recommendationsys.data.FakeDatabase
import com.example.recommendationsys.data.entity.UserPreference

interface UserPrefDao {
    fun getUserPreferenceByUserId(userId: String): UserPreference?
    fun saveUserPreference(userPreference: UserPreference)
}

class UserPrefDaoImpl : UserPrefDao {
    override fun getUserPreferenceByUserId(userId: String): UserPreference? {
        return FakeDatabase.getUserPreferenceByUserId(userId)
    }

    override fun saveUserPreference(userPreference: UserPreference) {
        FakeDatabase.saveUserPreference(userPreference)
    }
}
