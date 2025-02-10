package com.example.recommondationsys.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recommondationsys.databinding.ItemChatMessageBinding
import android.content.Context
import android.widget.TextView
import com.example.recommondationsys.R
import com.example.recommondationsys.data.model.ChatMessage
import com.example.recommondationsys.data.model.MessageType

class ChatAdapter(private val context: Context, private val chatMessages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.userMessageText.visibility = View.GONE
            binding.systemMessageText.visibility = View.GONE
            binding.restaurantMessageLayout.visibility = View.GONE

            when (message.type) {
                MessageType.USER -> {
                    binding.userMessageText.text = message.text
                    binding.userMessageText.visibility = View.VISIBLE
                }
                MessageType.SYSTEM -> {
                    binding.systemMessageText.text = message.text
                    binding.systemMessageText.visibility = View.VISIBLE
                }
                MessageType.RECOMMENDATION -> {
                    // 根据消息内容，区分是推荐餐厅还是收藏餐厅
                    binding.restaurantMessageTitle.text = if (message.text.contains("收藏")) {
                        "您的收藏餐厅如下："
                    } else {
                        "推荐的餐厅如下："
                    }
                    if (message.recommendations.isNullOrEmpty()) {
                        binding.restaurantMessageLayout.visibility = View.GONE
                    } else {
                        binding.restaurantMessageLayout.visibility = View.VISIBLE

                        // 清除旧的推荐??
                        binding.restaurantListContainer.removeAllViews()
                        // 动态添加餐厅
                        // message.recommendations为空，是否要隐藏restaurantMessageLayout?
                        for (restaurant in message.recommendations) {
                            val restaurantView = LayoutInflater.from(context)
                                .inflate(R.layout.item_restaurant, binding.restaurantListContainer, false)

                            restaurantView.findViewById<TextView>(R.id.restaurantName).text = restaurant.name
                            binding.restaurantListContainer.addView(restaurantView)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatMessages[position])
    }

    override fun getItemCount(): Int = chatMessages.size
}
