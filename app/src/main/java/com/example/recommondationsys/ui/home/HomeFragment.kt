package com.example.recommendationsys.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommendationsys.data.network.UserManager
import com.example.recommondationsys.databinding.FragmentHomeBinding
import com.example.recommondationsys.ui.home.ChatAdapter
import com.example.recommondationsys.data.model.ChatMessage
import com.example.recommondationsys.data.model.MessageType
import com.example.recommondationsys.ui.restaurant.RestaurantAdapter
import com.example.recommondationsys.ui.restaurant.RestaurantViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var restaurantAdapter: RestaurantAdapter

    private val restaurantViewModel: RestaurantViewModel by viewModels()
    private val userId: String get() = UserManager.getUser()!!.id

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView
        chatAdapter = ChatAdapter(requireContext(), chatMessages)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        restaurantAdapter = RestaurantAdapter(requireContext()) { restaurant ->
            restaurantViewModel.toggleFavorite(userId, restaurant)
        }

        // 监听 ViewModel 数据
        restaurantViewModel.restaurants.observe(viewLifecycleOwner, Observer {
            restaurantAdapter.updateList(it, it.filter { r -> r.isFavorite }.map { r -> r.placeId })
        })

        restaurantViewModel.favoriteRestaurants.observe(viewLifecycleOwner, Observer {
            restaurantAdapter.updateList(it, it.map { r -> r.placeId })
        })
        restaurantViewModel.chatMessages.observe(viewLifecycleOwner, Observer {
            chatMessages.clear()
            chatMessages.addAll(it)
            chatAdapter.notifyDataSetChanged()
        })

        // 发送查询
        binding.sendButton.setOnClickListener {
            //记录搜索次数
            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val searchCount = sharedPreferences.getInt("search_count", 0)
            sharedPreferences.edit().putInt("search_count", searchCount + 1).apply()

            val query = binding.inputField.text.toString()
            restaurantViewModel.fetchRecommendedRestaurants(query)
            // 先显示用户输入的消息
            val userMessage = ChatMessage(query, MessageType.USER)
            restaurantViewModel.addChatMessage(userMessage)  // 这里需要 `addChatMessage` 方法
        }

        // 获取收藏
        binding.getFavoritesButton.setOnClickListener {
            restaurantViewModel.loadFavoriteRestaurants(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
