package com.example.recommendationsys.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recommondationsys.R

class RecommendationAdapter(
    private val messages: MutableList<String>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.textView.text = message // 绑定消息内容到 TextView
        Log.d("RecommendationAdapter", "Binding message: $message at position $position")

        holder.itemView.setOnClickListener {
            onItemClicked(message) // 点击事件
        }
    }


    override fun getItemCount(): Int = messages.size

    fun refreshMessages(newMessages: List<String>) {
        Log.d("RecommendationAdapter", "Before clearing: ${messages.size}")

        messages.clear()
        Log.d("RecommendationAdapter", "After clearing: ${messages.size}")

        messages.addAll(newMessages)
        Log.d("RecommendationAdapter", "After adding: ${messages.size}")

        notifyDataSetChanged()
        Log.d("RecommendationAdapter", "Adapter refreshed with messages: $messages")
    }
}
