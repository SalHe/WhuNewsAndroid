package com.saheli.whu.news.ui.home

import androidx.annotation.StringRes
import com.saheli.whu.news.R

enum class Section(@StringRes val title: Int) {
    News(R.string.news_section),
    Favorite(R.string.favorite_section),
    Explorer(R.string.explorer_section)
}