package com.example.recommondationsys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recommendationsys.data.network.UserManager
import com.example.recommendationsys.data.network.UserPrefManager
import com.example.recommondationsys.ui.home.HomeActivity
import com.example.recommondationsys.data.model.UserPreference
import com.example.recommondationsys.R
import com.example.recommondationsys.data.model.User
import com.example.recommondationsys.data.network.UserDTO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val inputUsername = view.findViewById<EditText>(R.id.input_username)
        val inputPassword = view.findViewById<EditText>(R.id.input_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)

        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            lifecycleScope.launch {

                //调用loginuser，此时token和user对象都存在sharedpref里
                val success = UserManager.loginUser(username, password)
                if (success) {
                    Toast.makeText(requireContext(), "登录成功!", Toast.LENGTH_SHORT).show()

                    // 等待 UserManager.getUser() 返回非空
                    var user: UserDTO? = null
                    repeat(5) { // 最多等待 5 次
                        user = UserManager.getUser()
                        if (user != null) return@repeat
                        delay(200) // 每次等 200ms
                    }

                    if (user == null) {
                        Toast.makeText(requireContext(), "登录失败，无法获取用户信息!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val userId = user!!.id

                    // **如果用户没有偏好数据，则创建**
                    val userPref = UserPrefManager.getUserPreference(userId)
                    if (userPref == null) {
                        UserPrefManager.saveUserPreference(
                            userId,
                            UserPreference(id = UUID.randomUUID().toString(), userId = userId)
                        )
                    }

                    val targetActivity = if (user!!.isNewUser) PrefActivity::class.java else HomeActivity::class.java
                    startActivity(Intent(requireContext(), targetActivity).apply {
                        putExtra("userId", userId)
                    })

                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "登录失败!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegister.setOnClickListener {
            (activity as? AuthActivity)?.switchToRegister()
        }

        return view
    }
}
