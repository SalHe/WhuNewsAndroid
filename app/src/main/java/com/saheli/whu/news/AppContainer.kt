package com.saheli.whu.news

import android.content.SharedPreferences
import com.saheli.whu.news.api.NewsService
import com.saheli.whu.news.db.AppDatabase

interface AppContainer {
    val newsService: NewsService
    val appDatabase: AppDatabase
    val sharedPreferences: SharedPreferences
}

