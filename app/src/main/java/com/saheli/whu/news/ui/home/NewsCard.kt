package com.saheli.whu.news.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.glide.rememberGlidePainter
import com.saheli.whu.news.api.News
import com.saheli.whu.news.db.DataGenerator

@Composable
fun NewsCard(
    news: News,
    modifier: Modifier = Modifier,
    toggleFavorite: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberGlidePainter(news.image),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
        )
        Spacer(modifier = Modifier.padding(bottom = 10.dp))


        Row(
            horizontalArrangement = Arrangement.End
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = news.passtime,
                    style = MaterialTheme.typography.subtitle1.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            IconToggleButton(checked = news.favorite, onCheckedChange = toggleFavorite) {
                Image(
                    imageVector = if (news.favorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun NewsCardPreview() {
    NewsCard(news = DataGenerator.news[0])
}