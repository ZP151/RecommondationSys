package com.example.recommendationsys.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommendationsys.databinding.FragmentRecommendationBinding
import com.example.recommendationsys.ui.adapters.RestaurantAdapter
import com.example.recommendationsys.viewmodel.FavoriteViewModel
import com.example.recommendationsys.viewmodel.RecommendationViewModel

class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!
    private val recommendationViewModel: RecommendationViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: RestaurantAdapter
    private val userId = "user123"  // Replace with real user ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RestaurantAdapter { restaurant ->
            favoriteViewModel.toggleFavorite(userId, restaurant)
        }

        binding.recyclerViewRecommendation.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecommendation.adapter = adapter

        recommendationViewModel.recommendations.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list, favoriteViewModel.favorites.value?.map { it.placeId } ?: emptyList())
        }

        favoriteViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.updateList(recommendationViewModel.recommendations.value ?: emptyList(), favorites.map { it.placeId })
        }

        recommendationViewModel.fetchRecommendations()
        favoriteViewModel.fetchFavorites(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

