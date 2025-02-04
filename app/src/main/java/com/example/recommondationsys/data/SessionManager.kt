/*
package com.example.recommondationsys.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(username: String) {
        prefs.edit().putString("logged_in_user", username).apply()
    }

    fun getUser(): String? {
        return prefs.getString("logged_in_user", null)
    }

    fun isLoggedIn(): Boolean {
        return getUser() != null
    }

    fun logout() {
        prefs.edit().remove("logged_in_user").apply()
    }
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
*/
