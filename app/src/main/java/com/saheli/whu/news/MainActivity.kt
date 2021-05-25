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
import com.saheli.whu.news.ui.WhuNewsApp
import com.saheli.whu.news.ui.theme.WhuNewsTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import kotlin.coroutines.CoroutineContext

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

        val appContainer: AppContainer = AppContainerImpl(newsService, appDatabase)

        setContent {
            WhuNewsApp(appContainer)
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