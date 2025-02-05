package com.example.recommendationsys.data

import android.content.Context
import android.content.SharedPreferences
import com.example.recommendationsys.data.dao.UserPrefDao
import com.example.recommendationsys.data.dao.UserPrefDaoImpl
import com.example.recommendationsys.data.entity.UserPreference
import java.util.UUID

object UserPrefManager {
    private const val PREF_NAME = "user_pref_settings"
    private const val KEY_CURRENT_USER_ID = "current_user_id"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPrefDao: UserPrefDao

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        userPrefDao = UserPrefDaoImpl() // 未来可以换成 Room 实现
    }

    /** 记录当前用户 ID */
    fun setCurrentUser(userId: String) {
        sharedPreferences.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    /** 获取当前用户的 ID */
    fun getCurrentUserId(): String? {
        return sharedPreferences.getString(KEY_CURRENT_USER_ID, null)
    }

    /** 获取当前用户偏好设置 */
    fun getUserPreference(): UserPreference {
        val userId = getCurrentUserId() ?: return UserPreference(id = "default", userId = "default")
        return userPrefDao.getUserPreferenceByUserId(userId)
            ?: UserPreference(id = UUID.randomUUID().toString(), userId = userId)
    }

    /** 保存用户偏好设置 */
    fun saveUserPreference(userPreference: UserPreference) {
        userPrefDao.saveUserPreference(userPreference)
    }

    /** 退出时清除 */
    fun logout() {
        sharedPreferences.edit().remove(KEY_CURRENT_USER_ID).apply()
    }
}
