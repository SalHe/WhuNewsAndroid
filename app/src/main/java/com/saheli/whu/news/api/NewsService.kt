package com.saheli.whu.news.api

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import retrofit2.http.GET
import retrofit2.http.Query

data class NetEaseResponse<T>(
    val code: Int,
    val message: String,
    val result: T
) {
    operator fun invoke(): T = result
}

@Entity(
    indices = [
        Index(value = ["title", "path"], unique = true)
    ]
)
data class News(
    @PrimaryKey(autoGenerate = true)
    @JsonIgnoreProperties
    val id: Int? = null,
    val image: String,
    val passtime: String,
    val path: String,
    val title: String,
    @JsonIgnoreProperties
    val favorite: Boolean = false
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