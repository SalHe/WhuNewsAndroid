package com.saheli.whu.news.ui.home

import androidx.compose.runtime.Composable

internal data class TabContent(
    val section: Section,
    val content: @Composable () -> Unit
)