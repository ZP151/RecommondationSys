package com.example.recommondationsys.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recommondationsys.R
import com.example.recommondationsys.data.User
import com.example.recommondationsys.data.UserManager

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val inputUsername = view.findViewById<EditText>(R.id.input_new_username)
        val inputPassword = view.findViewById<EditText>(R.id.input_new_password)
        val btnRegister = view.findViewById<Button>(R.id.btn_submit_register)

        btnRegister.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            val newUser = User(username, password)
            if (UserManager.registerUser(newUser)) {
                Toast.makeText(requireContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show()
                (activity as? AuthActivity)?.switchToLogin()
            } else {
                Toast.makeText(requireContext(), "Username already exists", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
