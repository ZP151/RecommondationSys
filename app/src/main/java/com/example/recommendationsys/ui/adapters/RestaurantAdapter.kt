package com.example.recommendationsys.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationsys.R
import com.example.recommendationsys.data.model.Restaurant
import com.example.recommendationsys.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private val onFavoriteClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private val restaurantList = mutableListOf<Restaurant>()
    private val favoritePlaceIds = mutableSetOf<String>()

    fun updateList(newList: List<Restaurant>, favoriteIds: List<String>) {
        restaurantList.clear()
        restaurantList.addAll(newList)
        favoritePlaceIds.clear()
        favoritePlaceIds.addAll(favoriteIds)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false // 控制展开状态

        fun bind(restaurant: Restaurant) {
            binding.restaurantName.text = restaurant.name
            binding.restaurantRating.text = restaurant.rating.toString()
            binding.restaurantAddress.text = restaurant.address
            binding.restaurantPhone.text = restaurant.phoneNumber

            val isFavorite = favoritePlaceIds.contains(restaurant.placeId)
            binding.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.icon_favorite else R.drawable.icon_not_favorite
            )

            binding.favoriteButton.setOnClickListener {
                onFavoriteClick(restaurant)
            }

            // 展开详情
            binding.expandButton.setOnClickListener {
                isExpanded = !isExpanded
                binding.detailsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
                binding.expandButton.setImageResource(
                    if (isExpanded) R.drawable.icon_arrow_less else R.drawable.icon_arrow_detail
                )
            }

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
}

