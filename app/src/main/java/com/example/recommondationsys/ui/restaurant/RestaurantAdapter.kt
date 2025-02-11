package com.example.recommondationsys.ui.restaurant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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
        restaurantList.addAll(newList.take(5))
        favoritePlaceIds.clear()
        favoritePlaceIds.addAll(favoriteIds)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.isClickable = false
            itemView.isFocusable = false
        }
        fun bind(restaurant: Restaurant) {
            // 设置基本信息，这里有问题？
            binding.restaurantName.text = restaurant.name
            binding.restaurantRating.text = "${restaurant.rating} (${restaurant.userRatingsTotal})"
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
                else -> ""
            }

            // 收藏按钮状态
            val isFavorite = favoritePlaceIds.contains(restaurant.placeId)
            binding.favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_not_favorite
            )

            // 处理收藏点击事件
            binding.favoriteIcon.setOnClickListener {
                println(" favorite clicked")
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
            binding.expandButton.isEnabled = true
            val isExpanded = expandedMap[restaurant.placeId] ?: false
//            val isExpanded = binding.expandButton.tag
            binding.detailsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.expandButton.setImageResource(
                if (isExpanded) R.drawable.ic_arrow_less else R.drawable.ic_arrow_more
            )

            binding.expandButton.setOnClickListener(null) // 先清除旧的监听器
            // 处理展开/收起点击事件
            binding.expandButton.setOnClickListener {
                println("expand clicked")
                Toast.makeText(binding.root.context, "expandButton clicked!", Toast.LENGTH_SHORT).show()
                val newExpandedState = !(expandedMap[restaurant.placeId] ?: false)
                expandedMap[restaurant.placeId] = newExpandedState
                // 更新 UI
                binding.detailsLayout.visibility = if (newExpandedState) View.VISIBLE else View.GONE
                binding.expandButton.setImageResource(if (newExpandedState) R.drawable.ic_arrow_less else R.drawable.ic_arrow_more)

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isClickable = false // 禁止拦截点击事件
        binding.root.isFocusable = false
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(restaurantList[position])
    }

    override fun getItemCount(): Int = restaurantList.size
}
