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

fun <T> Channel<T>.sendEvent(event: T, viewModel: ViewModel? = null) {
    viewModel.let {
        viewModel?.viewModelScope?.launch(Dispatchers.Main.immediate) {
            this@sendEvent.send(event)
        }
    } ?: run {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            this@sendEvent.send(event)
        }
    }
}

fun <T> Channel<T>.sendSideEffect(viewModel: ViewModel, sideEffect: T) {
    viewModel.viewModelScope.launch(Dispatchers.Main.immediate) {
        this@sendSideEffect.send(sideEffect)
    }
}