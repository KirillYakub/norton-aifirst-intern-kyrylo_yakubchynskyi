package com.norton.securitydashboard.domain.model

data class RecommendationItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ItemIcon,
    val actionLabel: String
)
