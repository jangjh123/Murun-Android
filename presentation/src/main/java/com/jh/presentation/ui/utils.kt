package com.jh.presentation.ui

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

fun LifecycleOwner.repeatOnResumed(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED, block)
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

fun convertImage(byteArray: ByteArray): ImageBitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()
}