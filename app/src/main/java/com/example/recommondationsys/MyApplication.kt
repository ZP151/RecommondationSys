package com.example.recommondationsys

import android.app.Application
import com.example.recommondationsys.data.UserManager
import com.example.recommondationsys.data.UserPrefManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // ✅ 只初始化一次，避免 AuthActivity 反复初始化
        UserManager.init(this)
        UserPrefManager.init(this)
    }
}
