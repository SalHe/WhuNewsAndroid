package com.saheli.whu.news.ui

import androidx.compose.runtime.Composable
import com.saheli.whu.news.AppContainer
import com.saheli.whu.news.ui.home.HomeScreen
import com.saheli.whu.news.ui.theme.WhuNewsTheme

@Composable
fun WhuNewsApp(
    appContainer: AppContainer
) {
    WhuNewsTheme {
        HomeScreen(appContainer)
    }
}
