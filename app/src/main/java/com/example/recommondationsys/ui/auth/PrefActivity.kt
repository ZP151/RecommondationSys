package com.example.recommondationsys.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.recommondationsys.R
import com.example.recommondationsys.data.UserManager
import com.example.recommondationsys.data.UserPrefManager
import com.example.recommondationsys.data.entity.UserPreference
import com.example.recommondationsys.ui.home.HomeActivity
import java.util.UUID

class PrefActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        userId = intent.getStringExtra("userId") ?: return

        // ✅ 获取所有 RadioButton
        val radioButtons = listOf(
            findViewById<RadioButton>(R.id.radio_no_restrictions),
            findViewById<RadioButton>(R.id.radio_vegetarian),
            findViewById<RadioButton>(R.id.radio_gluten_free),
            findViewById<RadioButton>(R.id.radio_low_sugar)
        )

        // ✅ 让所有 RadioButton 互斥（手动管理单选）
        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }
        }

        findViewById<Button>(R.id.btn_save_pref).setOnClickListener {
            savePreferences()
            UserManager.setUserNotNew() // 标记用户已完成偏好设置
            Toast.makeText(this, "Preference saved！", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }

    /**
     * 获取 UI 组件的选择，并存入 UserPrefManager
     */
    private fun savePreferences() {
        val userPreference = UserPreference(
            id = UUID.randomUUID().toString(),
            userId = userId,
            dietPreference = getSelectedDietValue(), // ✅ 改成手动管理的 RadioButton 逻辑
            pricePreference = getSelectedRadioValue(R.id.radioGroupPrice),
            diningTime = getSelectedRadioValue(R.id.radioGroupDiningTime),
            restaurantType = getSelectedRadioValue(R.id.radioGroupRestaurantType),
            preferredCuisines = getCheckedValues(
                mapOf(
                    R.id.check_chinese to "Chinese",
                    R.id.check_western to "Western",
                    R.id.check_japanese to "Japanese",
                    R.id.check_korean to "Korean",
                    R.id.check_bbq to "BBQ",
                    R.id.check_fastfood to "FastFood"
                )
            ),
            transportMode = getCheckedValues(
                mapOf(
                    R.id.check_walk to "Walk",
                    R.id.check_bike to "Bike",
                    R.id.check_metro to "Subway",
                    R.id.check_car to "Drive"
                )
            ).firstOrNull() ?: "Walk"
        )

        // 存入 UserPrefManager
        UserPrefManager.saveUserPreference(userPreference)
    }

    /**
     * 获取选中的 RadioButton 的值
     */
    private fun getSelectedRadioValue(groupId: Int): String {
        val group = findViewById<RadioGroup>(groupId)
        val selectedId = group?.checkedRadioButtonId ?: return ""
        return findViewById<RadioButton>(selectedId)?.text.toString()
    }
    /**
     * 获取选中的 Dietary Preference（手动管理的 RadioButton 组）
     */
    private fun getSelectedDietValue(): String {
        val radioButtons = listOf(
            findViewById<RadioButton>(R.id.radio_no_restrictions),
            findViewById<RadioButton>(R.id.radio_vegetarian),
            findViewById<RadioButton>(R.id.radio_gluten_free),
            findViewById<RadioButton>(R.id.radio_low_sugar)
        )

        return radioButtons.firstOrNull { it.isChecked }?.text?.toString() ?: ""
    }

    /**
     * 获取所有选中的 CheckBox 值
     */
    private fun getCheckedValues(mapping: Map<Int, String>): List<String> {
        return mapping.filter { (id, _) -> findViewById<CheckBox>(id)?.isChecked == true }
            .values
            .toList()
    }

    /**
     * 完成设置后，跳转到 HomeActivity
     */
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
