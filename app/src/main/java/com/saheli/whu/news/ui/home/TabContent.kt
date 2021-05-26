package com.saheli.whu.news.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal data class TabContent(
    val section: Section,
    val tab: @Composable (title: String) -> Unit = {
        Text(
            text = it,
            modifier = Modifier.padding(vertical = 5.dp),
        )
    },
    val content: @Composable () -> Unit
)