package com.example.recommondationsys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recommondationsys.ui.home.HomeActivity
import com.example.recommondationsys.R
import com.example.recommondationsys.data.SessionManager

class AuthActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            Log.d("AuthActivity", "User already logged in, redirecting to MainActivity")

            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }else{
            Log.d("AuthActivity", "No session found, staying on AuthActivity")

        }

        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commit()
        }
    }
    // 切换到注册界面
    fun switchToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, RegisterFragment())
            .addToBackStack(null)  // 允许返回到登录界面
            .commit()
    }

    // 切换回登录界面
    fun switchToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, LoginFragment())
            .commit()
    }

//    // 处理用户成功登录
//    fun onUserLoggedIn(username: String) {
//        SessionManager.setUserLoggedIn(this, username)
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }
}
