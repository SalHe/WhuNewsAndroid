package com.saheli.whu.news.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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

        // TODO 通过标题查询收藏内容
        val favoriteTab = TabContent(
            Section.Favorite,
            tab = {
                Row {
                    Text(
                        text = it,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
                    )
                    if (favoriteNewsList.isNotEmpty()) {
                        Text(
                            text = favoriteNewsList.size.toString(),
                            color = Color.White,
                            modifier = Modifier
                                .background(MaterialTheme.colors.primary)
                                .clip(RoundedCornerShape(50))
                        )
                    }
                }
            }
        ) {
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
                        tabContent.tab(stringResource(id = tabContent.section.title))
                    }
                }
            }

            tabContents.forEachIndexed { index, tabContent ->
                Box(
                    modifier = if (index == selectedIndex) Modifier else Modifier.height(
                        0.dp
                    )
                ) { tabContent.content() }
            }
        }
    }
}

