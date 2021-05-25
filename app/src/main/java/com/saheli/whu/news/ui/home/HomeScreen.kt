package com.saheli.whu.news.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saheli.whu.news.AppContainer
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    appContainer: AppContainer
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) {

        val newsTab = TabContent(Section.News) {

        }
        val favoriteTab = TabContent(Section.Favorite) {
            // TODO 这里应该只展示收藏的内容，不过目前是测试阶段，先不管
            val newsList by appContainer.appDatabase.favoriteNewsDao().getNews()
                .collectAsState(listOf())
            LazyColumn {
                items(newsList) { news ->
                    NewsCard(
                        news = news,
                        modifier = Modifier.clickable {}
                    ) { isFavorite ->
                        coroutineScope.launch {
                            appContainer.appDatabase.favoriteNewsDao()
                                .updateNews(news.copy(favorite = isFavorite))
                        }
                    }
                }
            }
        }
        val tabContents = listOf(newsTab, favoriteTab)
        // val (selectedIndex, updateIndex) = rememberSaveable { mutableStateOf(0) }
        val (selectedIndex, updateIndex) = rememberSaveable { mutableStateOf(1) }

        Column {

            TabRow(
                selectedTabIndex = selectedIndex,
                backgroundColor = MaterialTheme.colors.onPrimary,
                contentColor = MaterialTheme.colors.primary
            ) {
                tabContents.forEachIndexed { index, tabContent ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { updateIndex(index) },
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                    ) {
                        Text(
                            text = stringResource(id = tabContent.section.title),
                            modifier = Modifier.padding(vertical = 5.dp),
                        )
                    }
                }
            }

            tabContents[selectedIndex].content()
        }
    }
}

