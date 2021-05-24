package com.saheli.whu.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.tooling.preview.Preview
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.saheli.whu.news.api.News
import com.saheli.whu.news.api.NewsService
import com.saheli.whu.news.ui.theme.WhuNewsTheme
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : ComponentActivity() {

    lateinit var newsService: NewsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newsService = Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .baseUrl(NewsService.BASE_URL)
            .build()
            .create(NewsService::class.java)

        setContent {
            WhuNewsTheme {

                val newsState = produceState(initialValue = listOf<News>()) {
                    val netEaseNews = newsService.getNetEaseNews(1)
                    value = netEaseNews.result
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {

                        Greeting("Android")

                        newsState.value.forEach {
                            Text(text = it.title)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WhuNewsTheme {
        Greeting("Android")
    }
}