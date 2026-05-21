package com.norton.securitydashboard.feature.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norton.securitydashboard.domain.model.CheckStatus
import com.norton.securitydashboard.domain.model.ScanCategory
import com.norton.securitydashboard.domain.model.ScanUpdate
import com.norton.securitydashboard.domain.repository.ScanRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ScanAction {
    data object OnBackClick : ScanAction
    data object OnExitConfirmed : ScanAction
    data object OnExitDismissed : ScanAction
    data object OnDeviceFixClick : ScanAction
    data object OnRetry : ScanAction
}

sealed interface ScanEvent {
    data object NavigateBack : ScanEvent
    data class ShowDeviceFixMessage(val message: String) : ScanEvent
}

class ScanViewModel(private val repository: ScanRepository) : ViewModel() {

    private val _state = MutableStateFlow(SmartScanState())
    val state = _state.asStateFlow()

    private val _events = Channel<ScanEvent>()
    val events = _events.receiveAsFlow()

    private var scanJob: Job? = null

    init {
        startScan()
    }

    fun onAction(action: ScanAction) {
        when (action) {
            ScanAction.OnBackClick -> {
                if (_state.value.phase == ScanPhase.Scanning) {
                    _state.update { it.copy(showExitDialog = true) }
                } else {
                    viewModelScope.launch { _events.send(ScanEvent.NavigateBack) }
                }
            }
            ScanAction.OnExitConfirmed -> {
                scanJob?.cancel()
                viewModelScope.launch { _events.send(ScanEvent.NavigateBack) }
            }
            ScanAction.OnExitDismissed -> {
                _state.update { it.copy(showExitDialog = false) }
            }
            ScanAction.OnDeviceFixClick -> {
                viewModelScope.launch {
                    _events.send(ScanEvent.ShowDeviceFixMessage(message = "It is only a prototype."))
                }
            }
            ScanAction.OnRetry -> startScan()
        }
    }

    private fun startScan() {
        scanJob?.cancel()
        _state.value = SmartScanState(phase = ScanPhase.Scanning)

        scanJob = viewModelScope.launch {
            repository.startScan()
                .catch { e ->
                    if (e !is CancellationException) {
                        _state.update { it.copy(phase = ScanPhase.Failed("An unexpected error occurred.")) }
                    }
                }
                .collect { update ->
                    when (update) {
                        is ScanUpdate.CheckResult -> handleCheckResult(update)
                        is ScanUpdate.Completed -> {
                            _state.update {
                                it.copy(
                                    phase = ScanPhase.Complete,
                                    progress = 1f,
                                    result = update.result,
                                    displayedScore = update.result.score
                                )
                            }
                        }
                        is ScanUpdate.Failed -> {
                            _state.update {
                                it.copy(phase = ScanPhase.Failed(update.error.name.lowercase().replace('_', ' ')))
                            }
                        }
                    }
                }
        }
    }

    private fun handleCheckResult(update: ScanUpdate.CheckResult) {
        val index = ScanCategory.entries.indexOf(update.check.category)
        val total = ScanCategory.entries.size

        val progress = when (update.check.status) {
            CheckStatus.SCANNING -> index.toFloat() / total
            else -> (index + 1).toFloat() / total
        }

        _state.update { state ->
            state.copy(
                checks = state.checks.map { existing ->
                    if (existing.category == update.check.category) update.check else existing
                },
                progress = progress
            )
        }
    }
}
