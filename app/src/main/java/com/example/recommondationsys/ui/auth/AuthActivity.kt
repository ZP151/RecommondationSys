package com.example.recommondationsys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recommondationsys.ui.PrefActivity
import com.example.recommondationsys.ui.home.HomeActivity
import com.example.recommondationsys.R
//import com.example.recommondationsys.data.SessionManager
import com.example.recommondationsys.data.UserManager
import com.example.recommondationsys.data.UserPrefManager

class AuthActivity : AppCompatActivity() {
    //private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        UserManager.init(this)
//        UserPrefManager.init(this)

        val user = UserManager.getCurrentUser()
        if (user != null) {
            Log.d("AuthActivity", "User logged in: ${user.username}, New User: ${user.isNewUser}")

            val targetActivity = if (user.isNewUser) PrefActivity::class.java else HomeActivity::class.java
            startActivity(Intent(this, targetActivity).apply {
                putExtra("userId", user.id)
            })
            finish()
            return
        }

        /*sessionManager = SessionManager(this)
//        sessionManager.clearSession()
        if (sessionManager.isLoggedIn()) {
            val username = sessionManager.getUser()

            Log.e("AuthActivity", "Loaded username from SessionManager: $username")

            if (username.isNullOrEmpty()) {
                Log.e("AuthActivity", "No username  found, staying in AuthActivity")
                return
            }

            val user = UserManager.getUserByUsername(username)
            Log.e("AuthActivity", "User found: $user")

            if (user == null) {
                Log.e("AuthActivity", "User not found, staying in AuthActivity")
                return
            }

            Log.d("AuthActivity", "User logged in: ${user.username}, New User: ${user.isNewUser}")

            //从后台进入的时候判断session，然后决定下一步activity
            val targetActivity = if (user.isNewUser) PrefActivity::class.java else HomeActivity::class.java
            startActivity(Intent(this, targetActivity).apply {
                putExtra("userId", user.id)
            })
            finish()
            return
        }*/

        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commit()
        }

    }

    fun switchToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun switchToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, LoginFragment())
            .commit()
    }
}
