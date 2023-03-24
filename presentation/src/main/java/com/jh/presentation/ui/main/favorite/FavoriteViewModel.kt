package com.jh.presentation.ui.main.favorite

import androidx.lifecycle.viewModelScope
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _sideEffectChannel = Channel<FavoriteSideEffect>()
    val sideEffectChannelFlow = _sideEffectChannel.receiveAsFlow()

    fun onClickGoToMain() {
        viewModelScope.launch(mainDispatcher) {
            _sideEffectChannel.send(FavoriteSideEffect.StartRunning)
        }
    }
}