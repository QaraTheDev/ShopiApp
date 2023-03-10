package com.example.shopiapp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.domain.entities.Brand
import com.example.shopiapp.databinding.HomeBrandsItemBinding

class HomeBrandsCollectionAdapter : ListAdapter<Brand, BrandViewHolder>(BrandsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        return BrandViewHolder(
            HomeBrandsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        Glide
            .with(holder.binding.root.context)
            .load(getItem(position).posterUrl)
            .into(holder.binding.poster)
    }
}

class BrandViewHolder(val binding: HomeBrandsItemBinding) : RecyclerView.ViewHolder(binding.root)

class BrandsDiffUtil : DiffUtil.ItemCallback<Brand>() {
    override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return newItem.posterUrl == oldItem.posterUrl
    }

    override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return newItem.posterUrl == oldItem.posterUrl
    }
}