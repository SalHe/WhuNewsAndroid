package com.saheli.whu.news.db.dao

import androidx.room.*
import com.saheli.whu.news.api.News
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(vararg news: News)

    @Delete
    suspend fun deleteNews(vararg news: News)

    @Update
    suspend fun updateNews(news: News)

    @Query("select * from news")
    fun getNews(): Flow<List<News>>

}