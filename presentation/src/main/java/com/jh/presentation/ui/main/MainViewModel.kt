package com.jh.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.use_case.music.GetMusicUseCase
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.CadenceType.ASSIGN
import com.jh.presentation.enums.CadenceType.TRACKING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getMusicUseCase: GetMusicUseCase
) : BaseViewModel() {

    private val eventChannel = Channel<MainEvent>()
    private val _sideEffectChannel = Channel<MainSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    val state: StateFlow<MainState> = eventChannel.receiveAsFlow()
        .runningFold(MainState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainState())

    private fun reduceState(state: MainState, event: MainEvent): MainState {
        return when (event) {
            is MainEvent.SkipToPrev -> {
                state.copy(isLoading = true)
            }
            is MainEvent.PlayOrPause -> {
                state.copy(isPlaying = !state.isPlaying)
            }
            is MainEvent.SkipToNext -> {
                state.copy()
            }
            is MainEvent.RepeatOne -> {
                state.copy(isRepeatingOne = !state.isRepeatingOne)
            }
            is MainEvent.TrackCadence -> {
                state.copy(cadenceType = TRACKING)
            }
            is MainEvent.AssignCadence -> {
                state.copy(cadenceType = ASSIGN)
            }
            is MainEvent.ShowSnackBar -> {
                state.copy(isSnackBarVisible = true)
            }
            is MainEvent.HideSnackBar -> {
                state.copy(isSnackBarVisible = false)
            }
            is MainEvent.StartRunning -> {
                state.copy(isRunning = true)
            }
            is MainEvent.StopRunning -> {
                state.copy(isRunning = false)
            }
            is MainEvent.OnCadenceMeasured -> {
                state.copy(cadence = event.cadence)
            }
        }
    }

    fun onClickSkipToPrev() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.SkipToPrev)
        }
    }

    fun onClickPlayOrPause() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.PlayOrPause)
        }
    }

    fun onClickSkipToNext() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.SkipToNext)
        }
    }

    fun onClickRepeatOne() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.RepeatOne)
        }
    }

    fun onClickTrackCadence() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.TrackCadence)
        }
    }

    fun onClickAssignCadence() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.AssignCadence)
        }
    }

    fun showSnackBar() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.ShowSnackBar)
        }
    }

    fun hideSnackBar() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.HideSnackBar)
        }
    }

    fun onClickStartRunning() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.StartRunning)
            _sideEffectChannel.send(MainSideEffect.TrackCadence)
        }
    }

    fun onClickStopRunning() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.StopRunning)
            _sideEffectChannel.send(MainSideEffect.StopTrackingCadence)
        }
    }

    fun onClickFavorite() {
        viewModelScope.launch(mainDispatcher) {
            _sideEffectChannel.send(MainSideEffect.GoToFavorite)
        }
    }

    fun onCadenceMeasured(cadence: Int) {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(MainEvent.OnCadenceMeasured(cadence))
        }
    }
}