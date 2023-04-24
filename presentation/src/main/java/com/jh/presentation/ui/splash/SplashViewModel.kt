package com.jh.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.use_case.splash.GetToSkipOnBoardingUseCase
import com.jh.murun.domain.use_case.splash.SetToSkipOnBoardingUseCase
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getToSkipOnBoardingUseCase: GetToSkipOnBoardingUseCase,
    private val setToSkipOnBoardingUseCase: SetToSkipOnBoardingUseCase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _sideEffectChannel = Channel<SplashSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    fun checkToSkipOnBoarding() {
        viewModelScope.launch(ioDispatcher) {
            getToSkipOnBoardingUseCase.invoke()
                .onEach { shouldSkip ->
                    if (shouldSkip) {
                        withContext(mainDispatcher) {
                            _sideEffectChannel.send(SplashSideEffect.SkipOnBoarding)
                        }
                    } else {
                        _sideEffectChannel.send(SplashSideEffect.NoSkipOnBoarding)
                        setToSkipOnBoardingUseCase.invoke()
                        cancel()
                    }
                }.catch {
                    setToSkipOnBoardingUseCase.invoke()
                    withContext(mainDispatcher) {
                        _sideEffectChannel.send(SplashSideEffect.NoSkipOnBoarding)
                    }
                }.launchIn(viewModelScope)
        }
    }
}