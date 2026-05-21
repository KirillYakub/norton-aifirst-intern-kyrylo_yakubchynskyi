package com.norton.securitydashboard.domain.model

data class ScanResult(
    val score: Int,
    val possibleRisks: List<RecommendationItem>,
    val recommendations: List<RecommendationItem>,
    val protectionUpgrades: List<RecommendationItem>
)
