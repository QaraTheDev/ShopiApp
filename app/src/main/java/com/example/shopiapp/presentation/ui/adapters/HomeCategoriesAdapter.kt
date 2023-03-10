package com.example.shopiapp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiapp.databinding.HomeCategoryItemBinding
import com.example.shopiapp.model.Category

class HomeCategoriesAdapter(
    private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoriesViewHolder>(CategoriesDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            HomeCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = getItem(position)
        with(holder.binding) {
            icon.setImageResource(category.drawableId)
            name.text = name.resources.getString(category.name)
            root.setOnClickListener { onItemClick(category) }
        }
    }
}

class CategoriesViewHolder(val binding: HomeCategoryItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class CategoriesDiffUtilCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.drawableId == newItem.drawableId
                && oldItem.name == newItem.name
    }
}