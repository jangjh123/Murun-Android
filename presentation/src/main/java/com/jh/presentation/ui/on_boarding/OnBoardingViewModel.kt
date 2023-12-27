package com.jh.presentation.ui.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jh.presentation.di.MainImmediateDispatcher
import com.jh.presentation.ui.on_boarding.OnBoardingContract.Effect.GoToMainActivity
import com.jh.presentation.ui.on_boarding.OnBoardingContract.Event.OnClickGoToMain
import com.jh.presentation.ui.on_boarding.OnBoardingContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher
) : OnBoardingContract, ViewModel() {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OnBoardingContract.Effect>()
    override val effect: SharedFlow<OnBoardingContract.Effect> = _effect.asSharedFlow()

    override fun event(event: OnBoardingContract.Event) = when (event) {
        is OnClickGoToMain -> onClickGoToMain()
    }

    private fun onClickGoToMain() {
        viewModelScope.launch(mainImmediateDispatcher) {
            _effect.emit(GoToMainActivity)
        }
    }
}