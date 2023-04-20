package com.jh.presentation.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.use_case.favorite.AddFavoriteMusicUseCase
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.CadenceType.ASSIGN
import com.jh.presentation.enums.CadenceType.TRACKING
import com.jh.presentation.ui.sendEvent
import com.jh.presentation.ui.sendSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val addFavoriteMusicUseCase: AddFavoriteMusicUseCase
) : BaseViewModel() {

    private val eventChannel = Channel<MainEvent>()
    private val _sideEffectChannel = Channel<MainSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    val state: StateFlow<MainState> = eventChannel.receiveAsFlow()
        .runningFold(MainState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainState())

    private fun reduceState(state: MainState, event: MainEvent): MainState {
        return when (event) {
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
            is MainEvent.SetAssignedCadence -> {
                state.copy(assignedCadence = event.cadence)
            }
            is MainEvent.SetMeasuredCadence -> {
                state.copy(measuredCadence = event.cadence)
            }
        }
    }

    fun onClickSkipToPrev() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.SkipToPrev)
    }

    fun onClickPlayOrPause() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.PlayOrPause)
    }

    fun onClickSkipToNext() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.SkipToNext)
    }

    fun onClickChangeRepeatMode() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.ChangeRepeatMode)
    }

    fun onClickTrackCadence() {
        sendEvent(eventChannel, MainEvent.TrackCadence)
    }

    fun onClickAssignCadence() {
        sendEvent(eventChannel, MainEvent.AssignCadence)
    }

    fun showSnackBar() {
        sendEvent(eventChannel, MainEvent.ShowSnackBar)
    }

    fun hideSnackBar() {
        sendEvent(eventChannel, MainEvent.HideSnackBar)
    }

    fun onClickStartRunning(cadence: Int?) {
        sendEvent(eventChannel, MainEvent.StartRunning)
        sendSideEffect(_sideEffectChannel, MainSideEffect.LaunchMusicPlayer)

        if (state.value.cadenceType == TRACKING) {
            sendSideEffect(_sideEffectChannel, MainSideEffect.TrackCadence)
        } else if (state.value.cadenceType == ASSIGN) {
            sendEvent(eventChannel, MainEvent.SetAssignedCadence(cadence!!))
        }
    }

    fun onClickStopRunning() {
        sendEvent(eventChannel, MainEvent.StopRunning)
        sendSideEffect(_sideEffectChannel, MainSideEffect.QuitMusicPlayer)

        if (state.value.cadenceType == TRACKING) {
            sendSideEffect(_sideEffectChannel, MainSideEffect.StopTrackingCadence)
        }
    }

    fun onClickFavorite() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.GoToFavorite)
    }

    fun onCadenceMeasured(cadence: Int) {
        sendEvent(eventChannel, MainEvent.SetMeasuredCadence(cadence))
    }

    fun onClickLikeOrDislike() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.LikeOrDislike)
    }

    fun likeOrDislikeMusic(music: Music?) {
        if (music != null) {
            viewModelScope.launch(ioDispatcher) {
                addFavoriteMusicUseCase(music).collect { result ->
                    when (result) {
                        true -> {
                            Log.d("FAVORITE", "Insertion Success.")
                        }
                        false -> {
                            Log.d("FAVORITE", "Insertion Failed.")
                        }
                    }
                }
            }
        }
    }
}