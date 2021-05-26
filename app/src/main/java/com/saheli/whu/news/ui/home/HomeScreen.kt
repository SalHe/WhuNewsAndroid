package com.saheli.whu.news.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.saheli.whu.news.AppContainer
import com.saheli.whu.news.utils.produceRefreshableState
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

        // 如果把状态放到TabContent中话，
        // 状态的生命周期就跟TabContent一样
        // 当切换tab的时候状态会丢失
        var currentPage by rememberSaveable { mutableStateOf(1) }
        val (result, onRefresh) = produceRefreshableState(appContainer) {
            val netEaseNewsResponse = newsService.getNetEaseNews(currentPage++)
            netEaseNewsResponse()
        }
        val favoriteNewsList by appContainer.appDatabase.favoriteNewsDao().getFavoriteNews()
            .collectAsState(listOf())

        val newsTab = TabContent(Section.News) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = result.value.loading),
                onRefresh = onRefresh
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    if (result.value.result != null) {
                        items(result.value.result!!) {
                            val news by appContainer.appDatabase.favoriteNewsDao().findNews(
                                it.path, it.title
                            ).collectAsState(initial = it)
                            NewsCard(
                                news = news ?: it,
                                modifier = Modifier.clickable {}
                            ) { isFavorite ->
                                // news = news.copy(favorite = isFavorite)
                                coroutineScope.launch {
                                    if (news == null) {
                                        appContainer.appDatabase.favoriteNewsDao()
                                            .insertNews(it.copy(favorite = isFavorite))
                                    } else {
                                        appContainer.appDatabase.favoriteNewsDao()
                                            .updateNews(news!!.copy(favorite = isFavorite))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val favoriteTab = TabContent(Section.Favorite) {
            LazyColumn {
                items(favoriteNewsList) { news ->
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
        val (selectedIndex, updateIndex) = rememberSaveable { mutableStateOf(0) }

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

