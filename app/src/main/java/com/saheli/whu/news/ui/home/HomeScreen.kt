package com.saheli.whu.news.ui.home

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.saheli.whu.news.AppContainer
import com.saheli.whu.news.R
import com.saheli.whu.news.utils.produceRefreshableState
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    appContainer: AppContainer
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var currentUrl by rememberSaveable { mutableStateOf("http://salheli.com") }

    Scaffold(
        scaffoldState = scaffoldState
    ) {

        // 当前TAB的选中项以及切换TAB的函数
        val (selectedIndex, updateIndex) = rememberSaveable { mutableStateOf(0) }

        // 通用的浏览网页调用
        val exploreUrl = { url: String ->
            currentUrl = url
            updateIndex(2)
        }

        val newsTab = TabContent(Section.News) {
            var currentPage by rememberSaveable { mutableStateOf(1) }
            val (result, onRefresh) = produceRefreshableState(appContainer) {
                val netEaseNewsResponse = newsService.getNetEaseNews(currentPage++)
                netEaseNewsResponse()
            }
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
                                modifier = Modifier.clickable { exploreUrl((news ?: it).path) }
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

        val favoriteNewsList by appContainer.appDatabase.favoriteNewsDao().getFavoriteNews()
            .collectAsState(listOf())
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
            var queryContent by rememberSaveable { mutableStateOf("") }
            Column {
                Row {
                    val keyboardController = LocalSoftwareKeyboardController.current

                    TextField(
                        value = queryContent,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        onValueChange = { queryContent = it },
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                        label = { Text(stringResource(id = R.string.query)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                LazyColumn {
                    items(favoriteNewsList.filter {
                        queryContent.isBlank()
                                || it.title.contains(queryContent)
                    }) { news ->
                        NewsCard(
                            news = news,
                            modifier = Modifier.clickable { exploreUrl(news.path) }
                        ) { isFavorite ->
                            coroutineScope.launch {
                                appContainer.appDatabase.favoriteNewsDao()
                                    .updateNews(news.copy(favorite = isFavorite))
                            }
                        }
                    }
                }
            }

        }

        val explorer = TabContent(Section.Explorer) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            // return super.shouldOverrideUrlLoading(view, url)
                            view?.loadUrl(url ?: "http://salheli.com")
                            return true
                        }
                    }
                }
            }) {
                it.loadUrl(currentUrl)
            }
        }
        val tabContents = listOf(newsTab, favoriteTab, explorer)

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

