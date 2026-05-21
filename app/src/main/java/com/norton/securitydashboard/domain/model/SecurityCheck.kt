package com.norton.securitydashboard.domain.model

data class SecurityCheck(
    val category: ScanCategory,
    val status: CheckStatus
)
