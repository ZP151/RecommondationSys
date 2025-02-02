package com.example.recommondationsys.data

import android.content.Context
import android.content.SharedPreferences
import com.example.recommondationsys.data.dao.UserDao
import com.example.recommondationsys.data.dao.UserDaoImpl
import com.example.recommondationsys.data.entity.User
import java.util.UUID

object UserManager {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_CURRENT_USER_ID = "current_user_id"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userDao: UserDao

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        userDao = UserDaoImpl() // 未来可以换成 Room 实现
    }


    /** 记录当前登录用户的 ID */
    fun setCurrentUser(userId: String) {
        sharedPreferences.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    /** 获取当前登录用户的 ID */
    fun getCurrentUserId(): String? {
        return sharedPreferences.getString(KEY_CURRENT_USER_ID, null)
    }

    /** 获取当前用户完整信息 */
    fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return userDao.getUserById(userId)
    }

    /** 退出登录，清除当前用户 */
    fun logout() {
        sharedPreferences.edit().remove(KEY_CURRENT_USER_ID).apply()
    }

    /** 注册用户 */
    fun registerUser(username: String, password: String): User? {
        if (userDao.getUserByUsername(username) != null) {
            return null // 用户已存在
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            username = username,
            password = password,
            isNewUser = true
        )

        userDao.addUser(newUser)
        setCurrentUser(newUser.id)
        return newUser
    }

    /** 验证登录 */
    fun validateUser(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username) ?: return false
        if (user.password == password) {
            setCurrentUser(user.id)
            return true
        }
        return false
    }

    /** 标记用户不是新用户 */
    fun setUserNotNew() {
        val user = getCurrentUser() ?: return
        val updatedUser = user.copy(isNewUser = false)
        userDao.updateUser(updatedUser)
    }

    /** 检查用户是否为新用户 */
    fun isNewUser(): Boolean {
        return getCurrentUser()?.isNewUser ?: true
    }
}
