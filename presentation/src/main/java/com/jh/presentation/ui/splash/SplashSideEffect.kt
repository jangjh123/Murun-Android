package com.jh.presentation.ui.splash

sealed class SplashSideEffect {
    object SkipOnBoarding : SplashSideEffect()
    object NoSkipOnBoarding : SplashSideEffect()
}