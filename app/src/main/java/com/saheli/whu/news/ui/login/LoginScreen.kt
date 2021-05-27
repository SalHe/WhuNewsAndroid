package com.saheli.whu.news.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.saheli.whu.news.R

enum class LoginStatus {
    VALIDATING, SUCCESS, ERROR
}

@Composable
fun LoginScreen(
    username: String = "",
    onLogin: (username: String, password: String, status: LoginStatus, SnackbarHostState) -> Boolean = { _, _, _, _ -> false }
) {

    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            var username by rememberSaveable { mutableStateOf(username) }
            var password by rememberSaveable { mutableStateOf("") }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(id = R.string.username)) }
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                TextButton(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(MaterialTheme.colors.primary),
                    onClick = {
                        if (onLogin(
                                username,
                                password,
                                LoginStatus.VALIDATING,
                                scaffoldState.snackbarHostState
                            )
                        ) {
                            onLogin(
                                username,
                                password,
                                LoginStatus.SUCCESS,
                                scaffoldState.snackbarHostState
                            )
                        } else {
                            onLogin(
                                username,
                                password,
                                LoginStatus.ERROR,
                                scaffoldState.snackbarHostState
                            )
                        }
                    }
                ) {
                    Text(
                        text = "登录",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }
}