package com.example.shopiapp.util

import com.test.domain.entities.GoodDetails
import com.example.shopiapp.model.GoodDetailsDto
import javax.inject.Inject

class GoodDetailsUtil @Inject constructor() {

    fun convertGoodDetailsToDto(goodDetails: GoodDetails): GoodDetailsDto {
        return GoodDetailsDto(
            goodDetails.name,
            goodDetails.description,
            goodDetails.rating,
            goodDetails.reviewsCount,
            goodDetails.price,
            goodDetails.colors,
            goodDetails.imageUrls
        )
    }
}