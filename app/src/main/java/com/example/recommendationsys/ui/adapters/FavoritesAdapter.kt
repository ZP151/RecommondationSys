package com.example.recommendationsys.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationsys.data.model.Restaurant
import com.example.recommendationsys.databinding.ItemRestaurantBinding

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    private var restaurantList: List<Restaurant> = listOf()

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            binding.favoriteRestaurantName.text = restaurant.name
            binding.favoriteRestaurantAddress.text = restaurant.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(restaurantList[position])
    }

    override fun getItemCount(): Int = restaurantList.size

    // 手动更新列表数据
    fun updateList(newList: List<Restaurant>) {
        restaurantList = newList
        notifyDataSetChanged() // 刷新整个列表（性能较差）
    }
}

