package me.mking.biirr.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Ready<T>(val data: T) : State<T>()
}

fun <T> Flow<T>.mapReadyState(): Flow<State.Ready<T>> = map { State.Ready(it) }
suspend fun <T> Flow<T>.collectInto(state: MutableStateFlow<T>) = collect { state.emit(it) }
