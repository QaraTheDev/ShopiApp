package com.example.shopiapp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopiapp.R
import com.example.shopiapp.databinding.GoodDetailsPosterItemBinding

class GoodDetailsPostersViewPagerAdapter(
    private val photoUrlList: List<String>
) : RecyclerView.Adapter<GoodDetailsPostersViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoodDetailsPostersViewHolder {
        return GoodDetailsPostersViewHolder(
            GoodDetailsPosterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return photoUrlList.size
    }

    override fun onBindViewHolder(holder: GoodDetailsPostersViewHolder, position: Int) {
        with(holder.binding) {
            Glide.with(root)
                .load(photoUrlList[position])
                .placeholder(R.drawable.profile_photo_place_holder)
                .into(goodPhoto)
        }
    }
}

class GoodDetailsPostersViewHolder(val binding: GoodDetailsPosterItemBinding) :
    RecyclerView.ViewHolder(binding.root)