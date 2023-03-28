package com.jh.presentation.ui.main.favorite

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.viewModelScope
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val eventChannel = Channel<FavoriteEvent>()
    private val _sideEffectChannel = Channel<FavoriteSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    val state: StateFlow<FavoriteState> = eventChannel.receiveAsFlow()
        .runningFold(FavoriteState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, FavoriteState())

    @OptIn(ExperimentalMaterialApi::class)
    private fun reduceState(state: FavoriteState, event: FavoriteEvent): FavoriteState {
        return when (event) {
            is FavoriteEvent.ShowMusicOption -> {
                state.copy(bottomSheetStateValue = ModalBottomSheetValue.Expanded)
            }
            is FavoriteEvent.HideMusicOption -> {
                state.copy(bottomSheetStateValue = ModalBottomSheetValue.Hidden)
            }
            is FavoriteEvent.InitBottomSheetState -> {
                state.copy(bottomSheetStateValue = null)
            }
        }
    }

    fun onClickShowMusicOption() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(FavoriteEvent.ShowMusicOption)
        }
    }

    fun onClickHideMusicOption() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(FavoriteEvent.HideMusicOption)
        }
    }

    fun onInitBottomSheetState() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(FavoriteEvent.InitBottomSheetState)
        }
    }

    fun onClickGoToMain() {
        viewModelScope.launch(mainDispatcher) {
            _sideEffectChannel.send(FavoriteSideEffect.StartRunning)
        }
    }
}