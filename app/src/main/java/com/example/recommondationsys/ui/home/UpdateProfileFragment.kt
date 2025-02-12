package com.example.recommondationsys.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recommendationsys.data.network.UserManager
import com.example.recommondationsys.databinding.FragmentUpdateProfileBinding
import kotlinx.coroutines.launch

class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 加载当前用户信息
        val currentUser = UserManager.getUser()
        currentUser?.let {
            binding.inputUsername.setText(it.username)
            binding.inputAge.setText(it.age?.toString() ?: "")
            binding.inputEmail.setText(it.email)
        }

        // 提交更新逻辑
        binding.btnSubmitUpdate.setOnClickListener {
            val newUsername = binding.inputUsername.text.toString().trim()
            val newAge = binding.inputAge.text.toString().toIntOrNull()
            val newEmail = binding.inputEmail.text.toString().trim()

            if (newUsername.isEmpty() || newAge == null || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val success = UserManager.updateUserProfile(newUsername, newAge, newEmail)
                if (success) {
                    Toast.makeText(requireContext(), "个人信息更新成功!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // 返回上一页面
                } else {
                    Toast.makeText(requireContext(), "个人信息更新失败，请稍后再试", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
