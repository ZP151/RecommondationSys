package com.example.recommondationsys.ui.restaurant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
import com.example.recommondationsys.R
import com.example.recommondationsys.data.model.Restaurant
import com.example.recommondationsys.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private val context: Context,
    private val onFavoriteClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private val restaurantList = mutableListOf<Restaurant>()
    private val favoritePlaceIds = mutableSetOf<String>()

    // 用 HashMap 存储展开状态，Key 是 placeId
    private val expandedMap = mutableMapOf<String, Boolean>()

    fun updateList(newList: List<Restaurant>, favoriteIds: List<String>) {
        restaurantList.clear()
        restaurantList.addAll(newList)
        favoritePlaceIds.clear()
        favoritePlaceIds.addAll(favoriteIds)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //private var isExpanded = false


        fun bind(restaurant: Restaurant) {
            // 设置基本信息，这里有问题？
            binding.restaurantName.text = restaurant.name
            binding.restaurantRating.text = "Rating: ${restaurant.rating} (${restaurant.userRatingsTotal})"
            binding.restaurantAddress.text = restaurant.address
            binding.restaurantPhone.text = restaurant.phoneNumber ?: "No phone available"
            binding.restaurantWebsite.text = restaurant.website ?: "No website available"

            // 价格等级（N/A 代表无数据）
            binding.restaurantPriceLevel.text = when (restaurant.priceLevel) {
//                0 -> "Free"
                1 -> "Budget"
                2 -> "Moderate"
                3 -> "Expensive"
                4 -> "Very Expensive"
                else -> "N/A"
            }

            // 收藏按钮状态
            val isFavorite = favoritePlaceIds.contains(restaurant.placeId)
            binding.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_not_favorite
            )

            // 处理收藏点击事件
            binding.favoriteButton.setOnClickListener {
                val newFavoriteStatus = !isFavorite
                if (newFavoriteStatus) {
                    favoritePlaceIds.add(restaurant.placeId)
                } else {
                    favoritePlaceIds.remove(restaurant.placeId)
                }

                notifyItemChanged(adapterPosition, "update_favorite")
                onFavoriteClick(restaurant)
            }

            // 设置展开状态
            val isExpanded = expandedMap[restaurant.placeId] ?: false
            binding.detailsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.expandButton.setImageResource(
                if (isExpanded) R.drawable.ic_arrow_less else R.drawable.ic_arrow_more
            )

            // 处理展开/收起点击事件
            binding.expandButton.setOnClickListener {
                val newExpandedState = !(expandedMap[restaurant.placeId] ?: false)
                expandedMap[restaurant.placeId] = newExpandedState

                // 立即更新 UI，防止复用问题
                notifyItemChanged(adapterPosition)
            }

            // 处理电话拨打
            binding.restaurantPhone.setOnClickListener {
                restaurant.phoneNumber?.let { phone ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                }
            }

            // 处理网站跳转
            binding.restaurantWebsite.setOnClickListener {
                restaurant.website?.let { website ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
                    context.startActivity(intent)
                }
            }

            /*// 加载餐厅图片（如果有）
            if (!restaurant.photoReference.isNullOrEmpty()) {
                val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${restaurant.photoReference}&key=YOUR_GOOGLE_API_KEY"
                Glide.with(binding.restaurantImage.context)
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(binding.restaurantImage)
            } else {
                binding.restaurantImage.setImageResource(R.drawable.placeholder_image)
            }*/
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
