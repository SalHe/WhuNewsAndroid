package com.saheli.whu.news

import com.saheli.whu.news.api.NewsService
import com.saheli.whu.news.db.AppDatabase

interface AppContainer {
    val newsService: NewsService
    val appDatabase: AppDatabase
}

