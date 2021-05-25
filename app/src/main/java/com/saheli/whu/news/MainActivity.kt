package com.saheli.whu.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.saheli.whu.news.api.News
import com.saheli.whu.news.api.NewsService
import com.saheli.whu.news.db.AppDatabase
import com.saheli.whu.news.db.DataGenerator
import com.saheli.whu.news.ui.theme.WhuNewsTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class MainActivity : ComponentActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = CoroutineName(this.javaClass.name)

    lateinit var newsService: NewsService
    private val appDatabase: AppDatabase by lazy { AppDatabase.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newsService = Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .baseUrl(NewsService.BASE_URL)
            .build()
            .create(NewsService::class.java)

        AppDatabase.databaseCreated.observe(this) {
            launch(Dispatchers.IO) {
                val news = appDatabase.favoriteNewsDao().getNews()
                launch(Dispatchers.Main) {

                }
            }
        }

        setContent {
            WhuNewsTheme {

                val newsState = produceState(initialValue = listOf<News>()) {
                    val netEaseNews = newsService.getNetEaseNews(1)
                    value = netEaseNews.result
                }

                val favoriteNews by appDatabase.favoriteNewsDao().getNews().collectAsState(listOf())

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {

                        Greeting("Android")

                        Text(text = "Insert One", Modifier.clickable {
                            launch(Dispatchers.IO) {
                                appDatabase.favoriteNewsDao().insertNews(DataGenerator.news[0].let {
                                    it.copy(id = null, title = "INSERT${it.title}")
                                })
                            }
                        })

                        favoriteNews.forEach {
                            Text(text = it.title)
                        }

                        // newsState.value.forEach {
                        //     Text(text = it.title)
                        // }
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