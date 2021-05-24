package com.saheli.whu.news.api

import retrofit2.http.GET
import retrofit2.http.Query

data class NetEaseResponse<T>(
    val code: Int,
    val message: String,
    val result: T
)

data class News(
    val image: String,
    val passtime: String,
    val path: String,
    val title: String
)


interface NewsService {

    companion object {
        const val BASE_URL = "https://api.apiopen.top/"
    }

    @GET("getWangYiNews")
    suspend fun getNetEaseNews(
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): NetEaseResponse<List<News>>

}