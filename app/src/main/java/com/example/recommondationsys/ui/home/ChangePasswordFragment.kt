package com.example.recommondationsys.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recommendationsys.data.network.UserManager
import com.example.recommondationsys.databinding.FragmentChangePasswordBinding
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmitChangePassword.setOnClickListener {
            handleChangePassword()
        }
    }

    private fun handleChangePassword() {
        val currentPassword = binding.inputCurrentPassword.text.toString().trim()
        val newPassword = binding.inputNewPassword.text.toString().trim()
        val confirmNewPassword = binding.inputConfirmNewPassword.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmNewPassword) {
            Toast.makeText(requireContext(), "新密码与确认密码不一致", Toast.LENGTH_SHORT).show()
            return
        }

        // 调用 UserManager 修改密码
        lifecycleScope.launch {
            val success = UserManager.changePassword(currentPassword, newPassword)
            if (success) {
                Toast.makeText(requireContext(), "密码修改成功!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "密码修改失败，请检查当前密码", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
