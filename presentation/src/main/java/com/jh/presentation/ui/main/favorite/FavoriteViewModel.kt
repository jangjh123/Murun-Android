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

    private val eventChannel = Channel<FavoriteUiEvent>()
    private val _sideEffectChannel = Channel<FavoriteSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    val state: StateFlow<FavoriteState> = eventChannel.receiveAsFlow()
        .runningFold(FavoriteState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, FavoriteState())

    @OptIn(ExperimentalMaterialApi::class)
    private fun reduceState(state: FavoriteState, event: FavoriteUiEvent): FavoriteState {
        return when (event) {
            is FavoriteUiEvent.OpenBottomSheet -> {
                state.copy(bottomSheetStateValue = ModalBottomSheetValue.Expanded)
            }
            is FavoriteUiEvent.CloseBottomSheet -> {
                state.copy(bottomSheetStateValue = ModalBottomSheetValue.Hidden)
            }
        }
    }

    fun onClickMusicOption() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(FavoriteUiEvent.OpenBottomSheet)
        }
    }

    fun onCloseMusicOption() {
        viewModelScope.launch(mainDispatcher) {
            eventChannel.send(FavoriteUiEvent.CloseBottomSheet)
        }
    }

    fun onClickGoToMain() {
        viewModelScope.launch(mainDispatcher) {
            _sideEffectChannel.send(FavoriteSideEffect.StartRunning)
        }
    }


}