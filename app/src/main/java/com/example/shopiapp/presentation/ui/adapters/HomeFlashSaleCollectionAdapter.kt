package com.example.shopiapp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.domain.entities.FlashSaleGood
import com.example.shopiapp.databinding.HomeFlashSaleItemBinding

class HomeFlashSaleCollectionAdapter(
    private val addToCart: (FlashSaleGood) -> Unit,
    private val addToFavourites: (FlashSaleGood) -> Unit,
    private val onGoodClick: (FlashSaleGood) -> Unit
) :
    ListAdapter<FlashSaleGood, FlashSaleViewHolder>(FlashSaleDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashSaleViewHolder {
        return FlashSaleViewHolder(
            HomeFlashSaleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FlashSaleViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {

            Glide.with(root).load(item.imageUrl).into(poster)

            discount.text = String.format("%d%% off", item.discount)
            category.text = item.category
            name.text = item.name
            price.text = String.format("$ %.3f", item.price)

            addToCartButton.setOnClickListener { addToCart(item) }
            addToFavouritesButton.setOnClickListener { addToFavourites(item) }
            root.setOnClickListener { onGoodClick(item) }
        }
    }
}

class FlashSaleViewHolder(val binding: HomeFlashSaleItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class FlashSaleDiffUtil : DiffUtil.ItemCallback<FlashSaleGood>() {

    override fun areItemsTheSame(oldItem: FlashSaleGood, newItem: FlashSaleGood): Boolean {
        return newItem.name == oldItem.name && newItem.price == oldItem.price
    }

    override fun areContentsTheSame(oldItem: FlashSaleGood, newItem: FlashSaleGood): Boolean {
        return newItem.name == oldItem.name
                && newItem.price == oldItem.price
                && newItem.category == oldItem.category
                && newItem.discount == oldItem.discount
                && newItem.imageUrl == oldItem.imageUrl
    }
}