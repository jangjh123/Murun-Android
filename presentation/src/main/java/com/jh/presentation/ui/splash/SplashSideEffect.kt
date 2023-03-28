package com.jh.presentation.ui.splash

sealed interface SplashSideEffect {
    object SkipOnBoarding : SplashSideEffect
    object NoSkipOnBoarding : SplashSideEffect
}