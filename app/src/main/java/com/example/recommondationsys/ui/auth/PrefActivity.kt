package com.example.recommondationsys.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.recommondationsys.R
import com.example.recommondationsys.data.UserManager
import com.example.recommondationsys.data.UserPrefManager
import com.example.recommondationsys.data.UserPreference
import com.example.recommondationsys.ui.home.HomeActivity
import java.util.UUID

class PrefActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        userId = intent.getStringExtra("userId") ?: return

        findViewById<Button>(R.id.btn_save_pref).setOnClickListener {
            savePreferences()
            UserManager.setUserNotNew(userId) // 标记用户已完成偏好设置
            Toast.makeText(this, "偏好已保存！", Toast.LENGTH_SHORT).show()
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
            dietPreference = getSelectedRadioValue(R.id.radioGroupDiet),
            pricePreference = getSelectedRadioValue(R.id.radioGroupPrice),
            diningTime = getSelectedRadioValue(R.id.radioGroupDiningTime),
            restaurantType = getSelectedRadioValue(R.id.radioGroupRestaurantType),
            preferredCuisines = getCheckedValues(
                mapOf(
                    R.id.check_chinese to "中餐",
                    R.id.check_western to "西餐",
                    R.id.check_japanese to "日料",
                    R.id.check_korean to "韩餐",
                    R.id.check_bbq to "烧烤",
                    R.id.check_fastfood to "快餐"
                )
            ),
            transportMode = getCheckedValues(
                mapOf(
                    R.id.check_walk to "步行",
                    R.id.check_bike to "骑车",
                    R.id.check_metro to "地铁",
                    R.id.check_car to "驾车"
                )
            ).firstOrNull() ?: "步行"
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
