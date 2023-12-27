package com.jh.presentation.ui.on_boarding

import androidx.lifecycle.ViewModel
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.ui.sendSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _sideEffectChannel = Channel<OnBoardingSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    fun onClickGoToMain() {
        sendSideEffect(_sideEffectChannel, OnBoardingSideEffect.GoToMainActivity)
    }
}