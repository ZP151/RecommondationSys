package com.example.recommondationsys

import android.app.Application
import com.example.recommendationsys.data.network.UserManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserManager.init(this)

        /*// ✅ 只初始化一次，避免 AuthActivity 反复初始化
        UserManager.init(this)
        UserPrefManager.init(this)*/
    }
}
