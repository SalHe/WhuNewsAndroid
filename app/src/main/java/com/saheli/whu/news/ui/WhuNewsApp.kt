package com.saheli.whu.news.ui

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import com.saheli.whu.news.AppContainer
import com.saheli.whu.news.ui.home.HomeScreen
import com.saheli.whu.news.ui.splash.SplashScreen
import com.saheli.whu.news.ui.theme.WhuNewsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import androidx.compose.animation.slideOutVertically as slideOutVertically1

@ExperimentalAnimationApi
@Composable
fun WhuNewsApp(
    appContainer: AppContainer
) {
    WhuNewsTheme {
        val inSplash by produceState(initialValue = true) {
            launch {
                delay(800L)
                value = false
            }
        }

        // TODO 效果需要调整一下，不过先实现再说
        AnimatedVisibility(visible = inSplash,
            exit = fadeOut()
        ) { SplashScreen() }
        AnimatedVisibility(
            visible = !inSplash,
            enter = fadeIn(initialAlpha = 0.3f)
        ) { HomeScreen(appContainer) }
    }
}
