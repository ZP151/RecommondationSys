package com.example.recommondationsys.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.recommendationsys.data.network.UserManager
import com.example.recommendationsys.ui.home.HomeFragment
import com.example.recommondationsys.R
import com.example.recommondationsys.databinding.ActivityHomeBinding
import com.example.recommondationsys.ui.auth.AuthActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val userId = intent.getStringExtra("userId")
        val userId = UserManager.getUserId()

        if (userId.isNullOrEmpty()) {
            Log.e("HomeActivity", "UserId 为空，跳转 AuthActivity")
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }else {
            Log.d("HomeActivity", "User 已登录: $userId")
        }
/*
        if (UserManager.getUserId() == null) {
            Log.d("HomeActivity", "检测到用户未登录，跳转到 AuthActivity")
            // 用户未登录，跳转到登录页面
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        } else {
            Log.d("HomeActivity", "User 已登录: ${UserManager.getUserId()}")
        }*/


        // 默认加载 HomeFragment
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, HomeFragment())
            }
        }

        // 点击个人资料按钮 -> 跳转到设置
        binding.profileIcon.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, SettingFragment())
                addToBackStack(null)
            }
        }
    }
}
