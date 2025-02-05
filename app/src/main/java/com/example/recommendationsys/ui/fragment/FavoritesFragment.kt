package com.example.recommendationsys.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommendationsys.databinding.FragmentFavoritesBinding
import com.example.recommendationsys.ui.adapters.RestaurantAdapter
import com.example.recommendationsys.viewmodel.FavoriteViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: RestaurantAdapter
    private val userId = "user123"  // 假设这里是用户ID（后续可以改为动态获取）

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView
        adapter = RestaurantAdapter(
            onFavoriteClick = { restaurant ->
            viewModel.toggleFavorite(userId, restaurant) // 传入 userId
            }
        )

        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

//         观察 ViewModel 里的收藏数据
        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list, list.map { it.placeId })
        }

        viewModel.fetchFavorites(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}