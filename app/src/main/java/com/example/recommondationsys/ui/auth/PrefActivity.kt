package com.example.recommondationsys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.recommendationsys.data.network.UserManager
import com.example.recommendationsys.data.network.UserPrefManager
import com.example.recommondationsys.ui.home.HomeActivity
import com.example.recommondationsys.R
import com.example.recommondationsys.data.model.UserPreference
import kotlinx.coroutines.launch
import java.util.UUID

class PrefActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)


        val radioButtons = listOf(
            findViewById<RadioButton>(R.id.radio_no_restrictions),
            findViewById<RadioButton>(R.id.radio_vegetarian),
            findViewById<RadioButton>(R.id.radio_gluten_free),
            findViewById<RadioButton>(R.id.radio_low_sugar)
        )

        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }
        }

        findViewById<Button>(R.id.btn_save_pref).setOnClickListener {
            savePreferences()
            Toast.makeText(this, "偏好设置已保存！", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }

    private fun savePreferences() {
        val userPreference = UserPreference(
            id = UUID.randomUUID().toString(),
            userId = userId,
            dietPreference = getSelectedDietValue(),
            pricePreference = getSelectedRadioValue(R.id.radioGroupPrice),
            preferredCuisines = getCheckedValues(
                mapOf(
                    R.id.check_chinese to "Chinese",
                    R.id.check_western to "Western",
                    R.id.check_japanese to "Japanese",
                    R.id.check_korean to "Korean",
                    R.id.check_bbq to "BBQ",
                    R.id.check_fastfood to "FastFood"
                )
            )
        )

        lifecycleScope.launch {
            // 更新  user 的 isNewUser
            UserManager.updateUserIsNew(false)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    /**
     * 获取选中的饮食偏好（Diet Preference）
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
     * 获取选中的 RadioButton 的值
     */
    private fun getSelectedRadioValue(groupId: Int): String {
        val group = findViewById<RadioGroup>(groupId)
        val selectedId = group?.checkedRadioButtonId ?: return ""
        return findViewById<RadioButton>(selectedId)?.text.toString()
    }
    /**
     * 获取用户勾选的美食偏好
     */
    private fun getCheckedValues(mapping: Map<Int, String>): List<String> {
        return mapping.filter { (id, _) -> findViewById<CheckBox>(id)?.isChecked == true }
            .values
            .toList()
    }

}
