package com.example.recommendationsys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recommendationsys.ui.home.HomeActivity
import com.example.recommendationsys.R
//import com.example.recommondationsys.data.SessionManager
import com.example.recommendationsys.data.UserManager
import com.example.recommendationsys.data.UserPrefManager
import com.example.recommendationsys.data.entity.UserPreference
import com.example.recommendationsys.ui.PrefActivity
import java.util.UUID

class LoginFragment : Fragment() {
    //private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        //sessionManager = SessionManager(requireContext())

        val inputUsername = view.findViewById<EditText>(R.id.input_username)
        val inputPassword = view.findViewById<EditText>(R.id.input_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)

        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            if (UserManager.validateUser(username, password)) { // ✅ 调用 validateUser() 进行密码验证
                val user = UserManager.getCurrentUser()!!  // ✅ 获取当前用户对象
                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()

                // **如果该用户没有 UserPreference，则创建**
                val userPref = UserPrefManager.getUserPreference()
                if (userPref == null) {
                    UserPrefManager.saveUserPreference(
                        UserPreference(
                            id = UUID.randomUUID().toString(),
                            userId = user.id
                        )
                    )
                }

/*

                val userId = UserManager.getCurrentUserId()
            //user.password == password
            if (userId != null ) {
                //sessionManager.saveUser(username)

                // **存储当前用户**
                UserManager.setCurrentUser(userId)

                // **获取用户对象**
                val user = UserManager.getCurrentUser()!!

                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()

                // **如果该用户没有 UserPreference，则创建**
                val userPref = UserPrefManager.getUserPreference()
                if (userPref == null) {
                    UserPrefManager.saveUserPreference(
                        UserPreference(
                            id = UUID.randomUUID().toString(),
                            userId = user.id
                        )
                    )
                }
*/

                //从登录页面决定下一步activity
                val targetActivity = if (user.isNewUser) PrefActivity::class.java else HomeActivity::class.java
                startActivity(Intent(requireContext(), targetActivity).apply {
                    putExtra("userId", user.id)
                })

                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            (activity as? AuthActivity)?.switchToRegister()
        }

        return view
    }
}
