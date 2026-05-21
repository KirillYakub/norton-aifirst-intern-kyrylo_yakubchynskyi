package com.norton.securitydashboard.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(val isRefreshing: Boolean = false)

sealed interface HomeAction {
    data object OnScanClick : HomeAction
    data object OnRefresh : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToScan : HomeEvent
}

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnScanClick -> viewModelScope.launch {
                _events.send(HomeEvent.NavigateToScan)
            }
            HomeAction.OnRefresh -> viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true) }
                delay(400)
                _state.update { it.copy(isRefreshing = false) }
                _events.send(HomeEvent.NavigateToScan)
            }
        }
    }
}
