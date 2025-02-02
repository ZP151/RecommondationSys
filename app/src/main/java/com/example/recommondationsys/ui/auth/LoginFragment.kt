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
import com.example.recommondationsys.ui.home.HomeActivity
import com.example.recommondationsys.R
import com.example.recommondationsys.data.SessionManager
import com.example.recommondationsys.data.UserManager
import com.example.recommondationsys.data.UserPrefManager
import com.example.recommondationsys.data.UserPreference
import com.example.recommondationsys.ui.PrefActivity
import java.util.UUID

class LoginFragment : Fragment() {
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        sessionManager = SessionManager(requireContext())

        val inputUsername = view.findViewById<EditText>(R.id.input_username)
        val inputPassword = view.findViewById<EditText>(R.id.input_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)

        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            val user = UserManager.getUserByUsername(username)

            if (user != null && user.password == password) {
                sessionManager.saveUser(username)

                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()

                // **如果该用户没有 UserPreference，则创建**
                val userPref = UserPrefManager.getUserPreference(user.id)
                if (userPref == null) {
                    UserPrefManager.saveUserPreference(
                        UserPreference(
                            id = UUID.randomUUID().toString(),
                            userId = user.id
                        )
                    )
                }

                val targetActivity = if (UserManager.isNewUser(username)) PrefActivity::class.java else HomeActivity::class.java
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
