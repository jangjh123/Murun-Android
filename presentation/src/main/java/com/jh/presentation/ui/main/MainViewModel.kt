package com.jh.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.use_case.favorite.AddFavoriteMusicUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.enums.LoadingMusicType.*
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    private val addFavoriteMusicUseCase: AddFavoriteMusicUseCase
) : ViewModel() {

    private val eventChannel = Channel<MainEvent>()
    private val _sideEffectChannel = Channel<MainSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    val state: StateFlow<MainState> = eventChannel.receiveAsFlow()
        .runningFold(MainState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainState())

    private fun reduceState(state: MainState, event: MainEvent): MainState {
        return when (event) {
            is MainEvent.TrackCadence -> {
                state.copy(loadingMusicType = TRACKING_CADENCE)
            }
            is MainEvent.AssignCadence -> {
                state.copy(loadingMusicType = ASSIGN_CADENCE)
            }
            is MainEvent.PlayFavoriteList -> {
                state.copy(loadingMusicType = FAVORITE_LIST)
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
        }
    }

    fun getIsStartedRunningWithFavoriteList() {
        if (savedStateHandle.get<Boolean>(MainActivity.KEY_IS_RUNNING_STARTED) == true) {
            sendEvent(eventChannel, MainEvent.PlayFavoriteList)
            startRunning()
            savedStateHandle.remove<Boolean>(MainActivity.KEY_IS_RUNNING_STARTED)
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
        if (state.value.loadingMusicType == TRACKING_CADENCE) {
            showToast("케이던스 트래킹 모드는 한 곡 반복을 사용할 수 없습니다.")
        } else {
            sendSideEffect(_sideEffectChannel, MainSideEffect.ChangeRepeatMode)
        }
    }

    fun onClickTrackCadence() {
        sendEvent(eventChannel, MainEvent.TrackCadence)
    }

    fun onClickAssignCadence() {
        sendEvent(eventChannel, MainEvent.AssignCadence)
    }

    fun onClickStartRunning(cadence: Int?) {
        startRunning()

        if (state.value.loadingMusicType == TRACKING_CADENCE) {
            sendSideEffect(_sideEffectChannel, MainSideEffect.TrackCadence)
        } else if (state.value.loadingMusicType == ASSIGN_CADENCE) {
            sendEvent(eventChannel, MainEvent.SetAssignedCadence(cadence!!))
        }
    }

    fun onClickStopRunning() {
        sendEvent(eventChannel, MainEvent.StopRunning)
        sendSideEffect(_sideEffectChannel, MainSideEffect.QuitMusicPlayer)

        if (state.value.loadingMusicType == TRACKING_CADENCE) {
            sendSideEffect(_sideEffectChannel, MainSideEffect.StopTrackingCadence)
        }
    }

    fun onClickFavorite() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.GoToFavorite)
    }

    fun onClickAddFavoriteMusic() {
        sendSideEffect(_sideEffectChannel, MainSideEffect.AddFavoriteMusic)
    }

    fun showToast(text: String) {
        sendSideEffect(_sideEffectChannel, MainSideEffect.ShowToast(text))
    }

    private fun startRunning() {
        sendEvent(eventChannel, MainEvent.StartRunning)
        sendSideEffect(_sideEffectChannel, MainSideEffect.LaunchMusicPlayer)
    }

    fun addFavoriteMusic(mediaItem: MediaItem?) {
        mediaItem?.mediaMetadata?.extras?.get("music").let { music ->
            viewModelScope.launch(ioDispatcher) {
                addFavoriteMusicUseCase(music as Music).onEach { result ->
                    when (result) {
                        true -> {
                            showToast("곡을 리스트에 추가하였습니다.")
                        }

                        false -> {
                            showToast("곡을 리스트에 저장할 수 없습니다.")
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }
}