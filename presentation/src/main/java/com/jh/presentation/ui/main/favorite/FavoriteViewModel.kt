package com.jh.presentation.ui.main.favorite

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.use_case.favorite.DeleteFavoriteMusicUseCase
import com.jh.murun.domain.use_case.favorite.GetFavoriteListUseCase
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.ui.sendEvent
import com.jh.presentation.ui.sendSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getFavoriteListUseCase: GetFavoriteListUseCase,
    private val deleteFavoriteMusicUseCase: DeleteFavoriteMusicUseCase
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
                state.copy(bottomSheetStateValue = ModalBottomSheetValue.Expanded, chosenMusic = event.music)
            }
            is FavoriteEvent.HideMusicOption -> {
                state.copy(bottomSheetStateValue = ModalBottomSheetValue.Hidden)
            }
            is FavoriteEvent.InitBottomSheetState -> {
                state.copy(bottomSheetStateValue = null)
            }
            is FavoriteEvent.LoadFavoriteList -> {
                state.copy(isLoading = true)
            }
            is FavoriteEvent.FavoriteListLoaded -> {
                state.copy(isLoading = false, favoriteList = event.favoriteList)
            }
            is FavoriteEvent.DeleteMusic -> {
                state.copy(isLoading = true)
            }
        }
    }

    init {
        loadFavoriteList()
    }

    private fun loadFavoriteList() {
        sendEvent(eventChannel, FavoriteEvent.LoadFavoriteList)
        viewModelScope.launch(ioDispatcher) {
            getFavoriteListUseCase().onEach {
                if (it != null) {
                    eventChannel.send(FavoriteEvent.FavoriteListLoaded(it))
                } else {
                    // TODO : Error Handling
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onClickShowMusicOption(music: Music) {
        sendEvent(eventChannel, FavoriteEvent.ShowMusicOption(music))
    }

    fun onClickHideMusicOption() {
        sendEvent(eventChannel, FavoriteEvent.HideMusicOption)
    }

    fun onInitBottomSheetState() {
        sendEvent(eventChannel, FavoriteEvent.InitBottomSheetState)
    }

    fun onClickGoToMain() {
        sendSideEffect(_sideEffectChannel, FavoriteSideEffect.StartRunning)
    }

    fun onClickDeleteMusic() {
        sendSideEffect(eventChannel, FavoriteEvent.DeleteMusic)
        deleteMusic()
    }

    fun showToast(text: String) {
        sendSideEffect(_sideEffectChannel, FavoriteSideEffect.ShowToast(text))
    }

    private fun deleteMusic() {
        viewModelScope.launch(ioDispatcher) {
            state.value.chosenMusic?.let {
                deleteFavoriteMusicUseCase(it).onEach { result ->
                    when (result) {
                        true -> {
                            sendEvent(eventChannel, FavoriteEvent.HideMusicOption)
                            loadFavoriteList()
                        }
                        false -> {

                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }
}