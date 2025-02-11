package com.example.recommondationsys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.recommendationsys.data.network.UserManager
import com.example.recommondationsys.ui.home.HomeActivity
import com.example.recommondationsys.R
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ 检查 SharedPreferences 是否已存储 userId
        val user = UserManager.getUser()
        if (user != null) {
            Log.d("AuthActivity", "已登录，检查 isNewUser")
            val targetActivity = if (user.isNewUser) PrefActivity::class.java else HomeActivity::class.java
            val intent = Intent(this, targetActivity)
            intent.putExtra("userId", user.id)
            startActivity(intent)
            finish()
        } else {
            Log.d("AuthActivity", "用户未登录，加载 login 界面")
            setContentView(R.layout.activity_auth)

        }

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
