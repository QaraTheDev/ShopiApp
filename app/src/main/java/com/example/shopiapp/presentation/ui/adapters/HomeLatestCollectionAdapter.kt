package com.example.shopiapp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.domain.entities.LatestGood
import com.example.shopiapp.databinding.HomeLatestItemBinding

class HomeLatestCollectionAdapter(
    private val addToCart: (LatestGood) -> Unit,
    private val onGoodClick: (LatestGood) -> Unit
) :
    ListAdapter<LatestGood, LatestGoodViewHolder>(LatestGoodDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestGoodViewHolder {
        return LatestGoodViewHolder(
            HomeLatestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LatestGoodViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            Glide.with(root).load(item.imageUrl).into(poster)

            category.text = item.category
            name.text = item.name
            price.text = String.format("$ %.3f", item.price)

            addToCartButton.setOnClickListener { addToCart(item) }
            root.setOnClickListener { onGoodClick(item) }
        }
    }
}

class LatestGoodViewHolder(val binding: HomeLatestItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class LatestGoodDiffUtil : DiffUtil.ItemCallback<LatestGood>() {

    override fun areItemsTheSame(oldItem: LatestGood, newItem: LatestGood): Boolean {
        return newItem.name == oldItem.name && newItem.price == oldItem.price
    }

    override fun areContentsTheSame(oldItem: LatestGood, newItem: LatestGood): Boolean {
        return newItem.name == oldItem.name
                && newItem.price == oldItem.price
                && newItem.category == oldItem.category
                && newItem.imageUrl == oldItem.imageUrl
    }
}