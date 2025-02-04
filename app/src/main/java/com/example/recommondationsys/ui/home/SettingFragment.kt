package com.example.recommondationsys.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.recommondationsys.R
//import com.example.recommondationsys.data.SessionManager
import com.example.recommondationsys.data.UserManager
import com.example.recommondationsys.data.UserPrefManager
import com.example.recommondationsys.ui.auth.AuthActivity

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val logoutButton: Button = view.findViewById(R.id.logout_button)


        logoutButton.setOnClickListener {
//            // 清除用户 Session
//            val sessionManager = SessionManager(requireContext())
//            sessionManager.clearSession()

            // **清除当前用户信息**
            UserManager.logout()
            UserPrefManager.logout()

            // 跳转到 AuthActivity，并清除当前任务栈，防止回退
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        return view
    }
}
