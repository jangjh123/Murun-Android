package com.jh.presentation.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.use_case.favorite.DeleteFavoriteMusicUseCase
import com.jh.murun.domain.use_case.favorite.GetFavoriteListUseCase
import com.jh.murun.domain.use_case.favorite.UpdateReorderedFavoriteMusicListUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainImmediateDispatcher
import com.jh.presentation.ui.main.favorite.FavoriteContract.Effect
import com.jh.presentation.ui.main.favorite.FavoriteContract.Effect.StartRunning
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickDeleteMusic
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickHideMusicOption
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickShowMusicOption
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickStartRunning
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnStarted
import com.jh.presentation.ui.main.favorite.FavoriteContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getFavoriteListUseCase: GetFavoriteListUseCase,
    private val deleteFavoriteMusicUseCase: DeleteFavoriteMusicUseCase,
    private val updateReorderedFavoriteMusicListUseCase: UpdateReorderedFavoriteMusicListUseCase
) : FavoriteContract, ViewModel() {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnStarted -> {
            onStarted()
        }

        is OnClickShowMusicOption -> {
            onClickShowMusicOption(event.music)
        }

        is OnClickHideMusicOption -> {
            onClickHideMusicOption()
        }

        is OnClickDeleteMusic -> {
            onClickDeleteMusic()
        }

        is OnClickStartRunning -> {
            onClickStartRunning()
        }
    }

    private fun onStarted() {
        _state.update {
            it.copy(isLoading = true)
        }

        loadFavoriteList()
    }

    private fun onClickShowMusicOption(music: Music) {
        _state.update {
            it.copy(
                isBottomSheetShowing = true,
                chosenMusic = music
            )
        }
    }

    private fun onClickHideMusicOption() {
        _state.update {
            it.copy(
                isBottomSheetShowing = false,
                chosenMusic = null
            )
        }
    }

    private fun loadFavoriteList() {
        getFavoriteListUseCase().onEach { favoriteList ->
            withContext(mainImmediateDispatcher) {
                if (favoriteList.isNullOrEmpty()) {
                    // todo: 예외 처리
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            favoriteList = favoriteList
                        )
                    }
                }
            }
        }.catch {
            // todo: 예외 처리
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun onClickDeleteMusic() {
        deleteMusic()
    }

    fun updateReorderedFavoriteList(musics: List<Music>) {
        viewModelScope.launch(ioDispatcher) {
            updateReorderedFavoriteMusicListUseCase(musics)
        }
    }

    private fun deleteMusic() {
        state.value.chosenMusic?.let { music ->
            deleteFavoriteMusicUseCase(music).onEach { isDeleted ->
                when (isDeleted) {
                    true -> {
                        loadFavoriteList()
                    }

                    false -> {
                        // todo : 예외 처리
                    }
                }
            }.catch {
                // todo : 예외 처리
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }

    private fun onClickStartRunning() {
        viewModelScope.launch {
            _effect.emit(StartRunning)
        }
    }
}