package com.example.recommondationsys.data

object UserManager {
    private val users = mutableListOf<User>(
        User("admin", "123"),  // 预设一个管理员账户
    )

    fun registerUser(user: User): Boolean {
        if (users.any { it.username == user.username }) {
            return false // 用户已存在
        }
        users.add(user)
        return true
    }

    fun validateUser(username: String, password: String): Boolean {
        return users.any { it.username == username && it.password == password }
    }
}
