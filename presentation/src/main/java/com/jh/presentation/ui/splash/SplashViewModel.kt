package com.jh.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.use_case.splash.GetToSkipOnBoardingUseCase
import com.jh.murun.domain.use_case.splash.SetToSkipOnBoardingUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainImmediateDispatcher
import com.jh.presentation.ui.splash.SplashContract.Effect
import com.jh.presentation.ui.splash.SplashContract.Effect.NoSkipOnBoarding
import com.jh.presentation.ui.splash.SplashContract.Effect.SkipOnBoarding
import com.jh.presentation.ui.splash.SplashContract.Event.OnStarted
import com.jh.presentation.ui.splash.SplashContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getToSkipOnBoardingUseCase: GetToSkipOnBoardingUseCase,
    private val setToSkipOnBoardingUseCase: SetToSkipOnBoardingUseCase,
) : SplashContract, ViewModel() {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: SplashContract.Event) = when (event) {
        OnStarted -> checkToSkipOnBoarding()
    }

    private fun checkToSkipOnBoarding() {
        getToSkipOnBoardingUseCase().onEach { isSkippable ->
            withContext(mainImmediateDispatcher) {
                when (isSkippable) {
                    true -> {
                        _effect.emit(SkipOnBoarding)
                    }

                    false -> {
                        _effect.emit(NoSkipOnBoarding)
                    }
                }
            }

            if (!isSkippable) {
                setToSkipOnBoardingUseCase()
            }
        }.catch {
            withContext(mainImmediateDispatcher) {
                _effect.emit(NoSkipOnBoarding)
            }

            setToSkipOnBoardingUseCase()
        }.launchIn(viewModelScope + ioDispatcher)
    }
}