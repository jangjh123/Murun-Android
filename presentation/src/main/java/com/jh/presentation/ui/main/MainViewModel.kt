package com.jh.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.use_case.favorite.AddFavoriteMusicUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainImmediateDispatcher
import com.jh.presentation.enums.LoadingMusicType.ASSIGN_CADENCE
import com.jh.presentation.enums.LoadingMusicType.TRACKING_CADENCE
import com.jh.presentation.ui.main.MainContract.Effect
import com.jh.presentation.ui.main.MainContract.Effect.GoToFavorite
import com.jh.presentation.ui.main.MainContract.Effect.ShowToast
import com.jh.presentation.ui.main.MainContract.Effect.TrackCadence
import com.jh.presentation.ui.main.MainContract.Event.OnClickAddFavoriteMusic
import com.jh.presentation.ui.main.MainContract.Event.OnClickAssignCadence
import com.jh.presentation.ui.main.MainContract.Event.OnClickFavorite
import com.jh.presentation.ui.main.MainContract.Event.OnClickStartRunning
import com.jh.presentation.ui.main.MainContract.Event.OnClickTrackCadence
import com.jh.presentation.ui.main.MainContract.Event.OnLongClickStopRunning
import com.jh.presentation.ui.main.MainContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    private val addFavoriteMusicUseCase: AddFavoriteMusicUseCase
) : MainContract, ViewModel() {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: MainContract.Event) = when (event) {
        is OnClickTrackCadence -> {
            onClickTrackCadence()
        }

        is OnClickAssignCadence -> {
            onClickAssignCadence()
        }

        is OnClickFavorite -> {
            onClickFavorite()
        }

        is OnClickStartRunning -> {
            onClickStartRunning()
        }

        is OnLongClickStopRunning -> {

        }

        is OnClickAddFavoriteMusic -> {

        }
    }

    private fun onClickTrackCadence() {
        _state.update {
            it.copy(loadingMusicType = TRACKING_CADENCE)
        }
    }

    private fun onClickAssignCadence() {
        _state.update {
            it.copy(loadingMusicType = ASSIGN_CADENCE)
        }
    }

    private fun onClickFavorite() {
        viewModelScope.launch {
            _effect.emit(GoToFavorite)
        }
    }

    private fun onClickStartRunning() {
        _state.update {
            it.copy(isRunning = true)
        }

        if (state.value.loadingMusicType == TRACKING_CADENCE) {
            viewModelScope.launch {
                _effect.emit(TrackCadence)
            }
        } else if (state.value.loadingMusicType == ASSIGN_CADENCE) {
            _state.update {
                it.copy(assignedCadence = state.value.assignedCadence)
            }
        }
    }

//    fun getIsStartedRunningWithFavoriteList() {
//        if (savedStateHandle.get<Boolean>(MainActivity.KEY_IS_RUNNING_STARTED) == true) {
//            sendEvent(eventChannel, MainEvent.PlayFavoriteList)
//            startRunning()
//            savedStateHandle.remove<Boolean>(MainActivity.KEY_IS_RUNNING_STARTED)
//        }
//    }
//
//    fun onClickSkipToPrev() {
//        sendSideEffect(_sideEffectChannel, MainSideEffect.SkipToPrev)
//    }
//
//    fun onClickPlayOrPause() {
//        sendSideEffect(_sideEffectChannel, MainSideEffect.PlayOrPause)
//    }
//
//    fun onClickSkipToNext() {
//        sendSideEffect(_sideEffectChannel, MainSideEffect.SkipToNext)
//    }
//
//    fun onClickChangeRepeatMode() {
//        if (state.value.loadingMusicType == TRACKING_CADENCE) {
//            showToast("케이던스 트래킹 모드는 한 곡 반복을 사용할 수 없습니다.")
//        } else {
//            sendSideEffect(_sideEffectChannel, MainSideEffect.ChangeRepeatMode)
//        }
//    }
//
//    fun onClickStopRunning() {
//        sendEvent(eventChannel, MainEvent.StopRunning)
//        sendSideEffect(_sideEffectChannel, MainSideEffect.QuitMusicPlayer)
//
//        if (state.value.loadingMusicType == TRACKING_CADENCE) {
//            sendSideEffect(_sideEffectChannel, MainSideEffect.StopTrackingCadence)
//        }
//    }
//
//    fun onClickAddFavoriteMusic() {
//        sendSideEffect(_sideEffectChannel, MainSideEffect.AddFavoriteMusic)
//    }
//
//    fun showToast(text: String) {
//        sendSideEffect(_sideEffectChannel, MainSideEffect.ShowToast(text))
//    }
//
//    private fun startRunning() {
//        sendEvent(eventChannel, MainEvent.StartRunning)
//        sendSideEffect(_sideEffectChannel, MainSideEffect.LaunchMusicPlayer)
//    }

    private fun addFavoriteMusic(mediaItem: MediaItem?) {
        mediaItem?.mediaMetadata?.extras?.get(MEDIA_TYPE).let { music ->
            addFavoriteMusicUseCase(music as Music).onEach { result ->
                withContext(mainImmediateDispatcher) {
                    _effect.emit(ShowToast(if (result) "곡을 리스트에 추가하였습니다." else "곡을 리스트에 저장할 수 없습니다."))
                }
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }

    companion object {
        private const val MEDIA_TYPE = "music"
    }
}