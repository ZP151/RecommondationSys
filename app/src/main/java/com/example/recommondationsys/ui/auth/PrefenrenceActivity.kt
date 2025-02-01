package com.example.recommondationsys.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recommondationsys.R

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val btnSavePreferences = findViewById<Button>(R.id.btn_save_preferences)
        btnSavePreferences.setOnClickListener {
            // 保存偏好设置
            Toast.makeText(this, "Preferences Saved", Toast.LENGTH_SHORT).show()
            finish() // 关闭当前界面，返回 MainActivity
        }
    }
}
