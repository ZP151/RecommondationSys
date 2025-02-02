package com.example.recommondationsys.data.dao


import com.example.recommondationsys.data.FakeDatabase
import com.example.recommondationsys.data.entity.UserPreference

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
