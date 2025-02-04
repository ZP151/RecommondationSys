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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationsys.ui.adapters.RecommendationAdapter
import com.example.recommondationsys.R
import com.example.recommondationsys.viewmodel.HomeViewModel


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecommendationAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initRecyclerView(view)
        initInputField(view)

//        val homeActivity = activity as? HomeActivity
//        homeActivity?.navigateToSettings()

        return view
    }

    private fun initInputField(view: View) {
        val inputField: EditText = view.findViewById(R.id.input_field)
        inputField.requestFocus()

        val sendButton: Button = view.findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            val input = inputField.text.toString().trim()
            if (input.isNotEmpty()) {
                Log.d("HomeFragment", "User input: $input")
                viewModel.addMessage("Sys: recommend restaurant based on your input -> $input")
                inputField.setText("")
            }
        }
    }

    private fun initRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recommendation_list)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecommendationAdapter(mutableListOf()) { message ->
            viewModel.addFavourite(message)
        }
        recyclerView.adapter = adapter

        viewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            adapter.refreshMessages(messages)
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputField = view.findViewById<EditText>(R.id.input_field)
        val sendButton = view.findViewById<Button>(R.id.send_button)

        Log.d("HomeFragment", "Checking UI Elements: InputField = $inputField, SendButton = $sendButton")

        if (inputField == null) {
            Log.e("HomeFragment", "EditText input_field NOT found in fragment_home.xml")
        } else {
            Log.d("HomeFragment", "EditText input_field successfully found")
            inputField.visibility = View.VISIBLE
        }
    }


}
