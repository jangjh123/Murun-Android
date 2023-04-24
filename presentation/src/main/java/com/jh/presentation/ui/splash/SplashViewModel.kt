package com.jh.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.use_case.splash.GetToSkipOnBoardingUseCase
import com.jh.murun.domain.use_case.splash.SetToSkipOnBoardingUseCase
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.ui.sendSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getToSkipOnBoardingUseCase: GetToSkipOnBoardingUseCase,
    private val setToSkipOnBoardingUseCase: SetToSkipOnBoardingUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _sideEffectChannel = Channel<SplashSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    fun checkToSkipOnBoarding() {
        viewModelScope.launch(ioDispatcher) {
            when (getToSkipOnBoardingUseCase().first()) {
                true -> {
                    sendSideEffect(_sideEffectChannel, SplashSideEffect.SkipOnBoarding)
                }
                false -> {
                    sendSideEffect(_sideEffectChannel, SplashSideEffect.NoSkipOnBoarding)
                    setToSkipOnBoardingUseCase()
                }
            }
        }
    }
}