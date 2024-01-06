package com.jh.presentation.ui.on_boarding

import com.jh.presentation.base.BaseContract

interface OnBoardingContract : BaseContract<OnBoardingContract.State, OnBoardingContract.Event, OnBoardingContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        object OnClickGoToMain : Event
    }

    sealed interface Effect {
        object GoToMainActivity : Effect
    }
}