package com.saheli.whu.news

import com.saheli.whu.news.api.NewsService
import com.saheli.whu.news.db.AppDatabase

class AppContainerImpl(
    override val newsService: NewsService,
    override val appDatabase: AppDatabase
) : AppContainer