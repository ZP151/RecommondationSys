package com.example.recommendationsys.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationsys.ui.adapters.RecommendationAdapter
import com.example.recommondationsys.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecommendationAdapter
    private val viewModel: HomeViewModel by viewModels() // 使用 ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recommendation_list)
        Log.d("HomeFragment", "RecyclerView visibility: ${recyclerView.visibility}")

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RecommendationAdapter(mutableListOf()) { message ->
            viewModel.addFavourite(message) // 添加到收藏
        }
        recyclerView.adapter = adapter
        Log.d("HomeFragment", "RecyclerView Adapter: $adapter")

        val inputField: EditText = view.findViewById(R.id.input_field)
        val sendButton: Button = view.findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            val input = inputField.text.toString().trim()
            if (input.isNotEmpty()) {
                //viewModel.addMessage("user: $input")
                viewModel.addMessage("Sys: recommend restaurant based on your input -> $input")
                inputField.setText("")
            }
        }

        // 观察 ViewModel 的 chatMessages
        viewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            Log.d("HomeFragment", "Received updated messages: $messages")
            adapter.refreshMessages(messages) // 刷新 RecyclerView
            Log.d("HomeFragment", "Refreshing RecyclerView with messages: ${viewModel.chatMessages.value}")
            Log.d("RecyclerView", "RecyclerView item count: ${recyclerView.adapter?.itemCount}")

            recyclerView.scrollToPosition(messages.size - 1)
        }

        return view
    }
}
