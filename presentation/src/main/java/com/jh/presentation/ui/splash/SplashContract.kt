package com.jh.presentation.ui.splash

import com.jh.presentation.base.BaseContract

interface SplashContract : BaseContract<SplashContract.State, SplashContract.Event, SplashContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        object OnStarted : Event
    }

    sealed interface Effect {

        object SkipOnBoarding : Effect

        object NoSkipOnBoarding : Effect
    }
}