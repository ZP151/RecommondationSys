package com.example.recommondationsys.data.dao

import com.example.recommondationsys.data.FakeDatabase
import com.example.recommondationsys.data.entity.User


interface UserDao {
    fun getUserById(userId: String): User?
    fun getUserByUsername(username: String): User?
    fun addUser(user: User)
    fun updateUser(user: User)
}

class UserDaoImpl : UserDao {
    override fun getUserById(userId: String): User? {
        return FakeDatabase.getUserById(userId)
    }

    override fun getUserByUsername(username: String): User? {
        return FakeDatabase.getUserByUsername(username)
    }

    override fun addUser(user: User) {
        FakeDatabase.addUser(user)
    }

    override fun updateUser(user: User) {
        FakeDatabase.updateUser(user)
    }
}
