package com.norton.securitydashboard.feature.scan

import androidx.compose.runtime.Stable
import com.norton.securitydashboard.domain.model.CheckStatus
import com.norton.securitydashboard.domain.model.ScanCategory
import com.norton.securitydashboard.domain.model.ScanResult
import com.norton.securitydashboard.domain.model.SecurityCheck

sealed interface ScanPhase {
    data object Idle : ScanPhase
    data object Scanning : ScanPhase
    data object Complete : ScanPhase
    data class Failed(val message: String) : ScanPhase
}

@Stable
data class SmartScanState(
    val phase: ScanPhase = ScanPhase.Idle,
    val progress: Float = 0f,
    val displayedScore: Int = 0,
    val checks: List<SecurityCheck> = ScanCategory.entries.map {
        SecurityCheck(it, CheckStatus.PENDING)
    },
    val result: ScanResult? = null,
    val showExitDialog: Boolean = false
)
