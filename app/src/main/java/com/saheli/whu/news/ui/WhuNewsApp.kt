package com.saheli.whu.news.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.saheli.whu.news.AppContainer
import com.saheli.whu.news.R
import com.saheli.whu.news.ui.home.HomeScreen
import com.saheli.whu.news.ui.login.LoginScreen
import com.saheli.whu.news.ui.login.LoginStatus
import com.saheli.whu.news.ui.splash.SplashScreen
import com.saheli.whu.news.ui.theme.WhuNewsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val KEY_USERNAME = "KEY_USERNAME"
private const val KEY_PASSWORD = "KEY_PASSWORD"

@OptIn(ExperimentalAnimationApi::class)
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
        val username = appContainer.sharedPreferences.getString(KEY_USERNAME, "")!!
        var hasLogin by rememberSaveable {
            // val password = appContainer.sharedPreferences.getString(KEY_PASSWORD, "")!!
            // mutableStateOf(validateUsernameAndPassword(username, password))
            mutableStateOf(false)
        }

        val coroutineScope = rememberCoroutineScope()

        val please_enter_right_username_password =
            stringResource(id = R.string.please_enter_right_username_password)
        val login_success =
            stringResource(id = R.string.login_success)


        // TODO 效果需要调整一下，不过先实现再说
        AnimatedVisibility(
            visible = inSplash,
            exit = fadeOut()
        ) { SplashScreen() }
        AnimatedVisibility(
            visible = !inSplash && !hasLogin,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoginScreen(username) { username, password, status, snackbarHostState ->
                when (status) {
                    LoginStatus.VALIDATING -> {
                        return@LoginScreen validateUsernameAndPassword(username, password)
                    }
                    LoginStatus.SUCCESS -> {
                        appContainer.sharedPreferences.edit {
                            putString(KEY_USERNAME, username)
                            putString(KEY_PASSWORD, password)
                        }
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(login_success)
                        }
                        hasLogin = true
                    }
                    LoginStatus.ERROR -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(please_enter_right_username_password)
                        }
                    }
                }
                false
            }
        }
        AnimatedVisibility(
            visible = hasLogin,
            enter = fadeIn()
        ) { HomeScreen(appContainer) }
    }
}

private fun validateUsernameAndPassword(username: String, password: String) =
    username.isNotBlank() && password == "123"