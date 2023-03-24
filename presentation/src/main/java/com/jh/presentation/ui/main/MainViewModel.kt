package com.jh.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.CadenceType.ASSIGN
import com.jh.presentation.enums.CadenceType.TRACKING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val eventChannel = Channel<MainUiEvent>()
    private val _sideEffectChannel = Channel<MainSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    val state: StateFlow<MainState> = eventChannel.receiveAsFlow()
        .runningFold(MainState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainState())

    private fun reduceState(state: MainState, event: MainUiEvent): MainState {
        return when (event) {
            is MainUiEvent.SkipToPrev -> {
                state.copy(isLoading = true)
            }
            is MainUiEvent.PlayOrPause -> {
                state.copy(isPlaying = !state.isPlaying)
            }
            is MainUiEvent.SkipToNext -> {
                state.copy()
            }
            is MainUiEvent.RepeatOne -> {
                state.copy(isRepeatingOne = !state.isRepeatingOne)
            }
            is MainUiEvent.TrackCadence -> {
                state.copy(cadenceType = TRACKING)
            }
            is MainUiEvent.AssignCadence -> {
                state.copy(cadenceType = ASSIGN)
            }
            is MainUiEvent.StartOrStopRunning -> {
                state.copy(isRunning = !state.isRunning)
            }
        }
    }

    fun onClickSkipToPrev() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.SkipToPrev)
        }
    }

    fun onClickPlayOrPause() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.PlayOrPause)
        }
    }

    fun onClickSkipToNext() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.SkipToNext)
        }
    }

    fun onClickRepeatOne() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.RepeatOne)
        }
    }

    fun onClickTrackCadence() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.TrackCadence)
        }
    }

    fun onClickAssignCadence() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.AssignCadence)
        }
    }

    fun onClickStartOrStopRunning() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainUiEvent.StartOrStopRunning)
        }
    }

    fun onClickFavorite() {
        viewModelScope.launch(mainDispatcher) {
            _sideEffectChannel.send(MainSideEffect.GoToFavorite)
        }
    }
}