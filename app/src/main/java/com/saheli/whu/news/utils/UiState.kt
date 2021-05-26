package com.saheli.whu.news.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch


data class RefreshableState<T>(
    val loading: Boolean,
    val result: T? = null
)

data class ProducerResult<T>(
    val result: State<T>,
    val onRefresh: () -> Unit
)

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun <Producer, T> produceRefreshableState(
    producer: Producer,
    key: Any? = null,
    produceFun: suspend Producer.() -> T
): ProducerResult<RefreshableState<T>> {

    val refreshChannel = remember { Channel<Unit>(Channel.CONFLATED) }

    val result =
        produceState(
            initialValue = RefreshableState<T>(loading = false),
            key1 = producer,
            key2 = produceFun,
            key3 = key
        ) {
            value = RefreshableState(loading = true)
            refreshChannel.send(Unit)

            launch {
                for (r in refreshChannel) {
                    value = value.copy(loading = true)
                    value = value.copy(loading = false, result = producer.produceFun())
                }
            }
        }

    return ProducerResult(
        result = result,
        onRefresh = { refreshChannel.offer(Unit) }
    )
}