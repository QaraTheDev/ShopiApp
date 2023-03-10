package com.example.shopiapp.util

import com.example.shopiapp.R
import com.example.shopiapp.model.Category

class CategoriesUtil {

    val categories =
        buildList {
            add(
                Category(
                    id = 0,
                    R.drawable.home_categories_phones_icon,
                    R.string.home_categories_phones_title
                )
            )
            add(
                Category(
                    id = 1,
                    R.drawable.home_categories_headphones_icon,
                    R.string.home_categories_headphones_title
                )
            )
            add(
                Category(
                    id = 2,
                    R.drawable.home_categories_games_icon,
                    R.string.home_categories_games_title
                )
            )
            add(
                Category(
                    id = 3,
                    R.drawable.home_categories_cars_icon,
                    R.string.home_categories_cars_title
                )
            )
            add(
                Category(
                    id = 4,
                    R.drawable.home_categories_furniture_icon,
                    R.string.home_categories_furniture_title
                )
            )
            add(
                Category(
                    id = 5,
                    R.drawable.home_categories_kids_icon,
                    R.string.home_categories_kids_title
                )
            )
        }.toList()
}