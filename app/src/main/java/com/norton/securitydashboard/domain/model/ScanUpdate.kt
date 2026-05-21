package com.norton.securitydashboard.domain.model

sealed interface ScanUpdate {
    data class CheckResult(val check: SecurityCheck) : ScanUpdate
    data class Completed(val result: ScanResult) : ScanUpdate
    data class Failed(val error: ScanError) : ScanUpdate
}
