package com.jh.presentation.ui.on_boarding

sealed interface OnBoardingSideEffect {
    object GoToMainActivity : OnBoardingSideEffect
}