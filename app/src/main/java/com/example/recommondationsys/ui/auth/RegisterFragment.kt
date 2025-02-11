package com.example.recommondationsys.ui.auth

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
import com.example.recommondationsys.R
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val inputUsername = view.findViewById<EditText>(R.id.input_new_username)
        val inputPassword = view.findViewById<EditText>(R.id.input_new_password)
        val inputConfirmPassword = view.findViewById<EditText>(R.id.input_confirm_password)

        val btnRegister = view.findViewById<Button>(R.id.btn_submit_register)

        btnRegister.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val success = UserManager.registerUser(username, password,confirmPassword)
                if (success) {
                    Toast.makeText(requireContext(), "注册成功!", Toast.LENGTH_SHORT).show()
                    (activity as? AuthActivity)?.switchToLogin()
                } else {
                    Toast.makeText(requireContext(), "用户名已存在", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
