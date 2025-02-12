package com.example.recommondationsys.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recommendationsys.data.network.UserManager
import com.example.recommondationsys.ui.auth.AuthActivity
import com.example.recommondationsys.R
import com.example.recommondationsys.databinding.FragmentSettingBinding
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置登出按钮逻辑
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                val success = UserManager.logout()
                if (success) {
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "登出失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 设置修改密码按钮逻辑
        binding.btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_changePasswordFragment)
        }
        binding.btnUpdateProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_updateProfileFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 避免内存泄漏
    }
}
