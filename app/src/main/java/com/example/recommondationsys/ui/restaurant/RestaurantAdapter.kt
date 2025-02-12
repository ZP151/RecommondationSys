package com.example.recommondationsys.ui.restaurant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationsys.data.network.RestaurantApiService
import com.example.recommendationsys.data.network.RetrofitInstance.api
import com.example.recommendationsys.data.network.UserManager
//import com.bumptech.glide.Glide
import com.example.recommondationsys.R
import com.example.recommondationsys.data.model.Restaurant
import com.example.recommondationsys.databinding.ItemRestaurantBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        restaurantList.addAll(newList.take(5))
        favoritePlaceIds.clear()
        favoritePlaceIds.addAll(favoriteIds)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant) {
            binding.restaurantName.text = restaurant.name
            binding.restaurantRating.text = "${restaurant.rating} (${restaurant.userRatingsTotal})"
            binding.restaurantAddress.text = restaurant.address
            binding.restaurantPhone.text = restaurant.phoneNumber ?: "No phone available"
            binding.restaurantWebsite.text = restaurant.website ?: "No website available"

            // 价格等级（N/A 代表无数据）
            binding.restaurantPriceLevel.text = when (restaurant.priceLevel) {
                1 -> "Budget"
                2 -> "Moderate"
                3 -> "Expensive"
                4 -> "Very Expensive"
                else -> "N/A"
            }

            // 收藏按钮状态
            val isFavorite = favoritePlaceIds.contains(restaurant.placeId)
            binding.favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_not_favorite
            )

            // 处理收藏点击事件
            binding.favoriteIcon.setOnClickListener {
                val newFavoriteStatus = !isFavorite
                if (newFavoriteStatus) {
                    favoritePlaceIds.add(restaurant.placeId)
                    addFavorite(restaurant)

                } else {
                    favoritePlaceIds.remove(restaurant.placeId)
                    removeFavorite(restaurant)

                }

                notifyItemChanged(adapterPosition, "update_favorite")
                onFavoriteClick(restaurant)
            }

            // 设置展开状态
            val isExpanded = expandedMap[restaurant.placeId] ?: true
            binding.detailsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.expandButton.setImageResource(
                if (isExpanded) R.drawable.ic_arrow_less else R.drawable.ic_arrow_more
            )

            // 处理展开/收起点击事件
            binding.expandButton.setOnClickListener {
                val newExpandedState = !(expandedMap[restaurant.placeId] ?: true)
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

            // 处理Google maps跳转
            binding.toMapIcon.setOnClickListener {
                val latitude = restaurant.latitude
                val longitude = restaurant.longitude
                val label = restaurant.name // 用餐厅名称作为标记

                val gmmIntentUri = Uri.parse("geo:0,0?q=$latitude,$longitude($label)")

                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps") // 强制使用 Google Maps 打开

                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    Toast.makeText(context, "Google Maps need to be downloaded.", Toast.LENGTH_SHORT).show()
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
        private fun addFavorite(restaurant: Restaurant) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = api.addFavorite(UserManager.getUser()!!.id, restaurant)
                    if (response.isSuccessful) {
                        favoritePlaceIds.add(restaurant.placeId)
                        launch(Dispatchers.Main) {
                            notifyItemChanged(adapterPosition)
                            Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun removeFavorite(restaurant: Restaurant) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = api.removeFavorite(UserManager.getUser()!!.id, restaurant.placeId)
                    if (response.isSuccessful) {
                        favoritePlaceIds.remove(restaurant.placeId)
                        launch(Dispatchers.Main) {
                            notifyItemChanged(adapterPosition)
                            Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
                    }
                }
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
