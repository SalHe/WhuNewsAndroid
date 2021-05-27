package com.saheli.whu.news.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.saheli.whu.news.BuildConfig

@Composable
fun SplashScreen() {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = BuildConfig.studentName,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h4
            )
            Text(
                text = BuildConfig.studentNumber,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }

}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}