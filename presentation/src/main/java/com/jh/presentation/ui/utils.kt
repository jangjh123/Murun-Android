package com.jh.presentation.ui

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

fun LifecycleOwner.repeatOnStarted(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state, block)
    }
}

fun <T> Channel<T>.sendEvent(event: T) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        this@sendEvent.send(event)
    }
}

fun <T> ViewModel.sendEvent(channel: Channel<T>, event: T) {
    viewModelScope.launch {
        channel.send(event)
    }
}

fun <T> ViewModel.sendSideEffect(channel: Channel<T>, sideEffect: T) {
    viewModelScope.launch {
        channel.send(sideEffect)
    }
}