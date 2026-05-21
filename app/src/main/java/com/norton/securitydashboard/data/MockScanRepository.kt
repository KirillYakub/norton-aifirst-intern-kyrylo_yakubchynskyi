package com.norton.securitydashboard.data

import com.norton.securitydashboard.domain.model.CheckStatus
import com.norton.securitydashboard.domain.model.ItemIcon
import com.norton.securitydashboard.domain.model.RecommendationItem
import com.norton.securitydashboard.domain.model.ScanCategory
import com.norton.securitydashboard.domain.model.ScanError
import com.norton.securitydashboard.domain.model.ScanResult
import com.norton.securitydashboard.domain.model.ScanUpdate
import com.norton.securitydashboard.domain.model.SecurityCheck
import com.norton.securitydashboard.domain.repository.ScanRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockScanRepository : ScanRepository {

    override fun startScan(): Flow<ScanUpdate> = flow {
        val completedChecks = mutableListOf<SecurityCheck>()
        var score = 100

        // 5 % chance of a fatal scan failure before any checks run
        if (Random.nextFloat() < 0.05f) {
            emit(ScanUpdate.Failed(ScanError.UNKNOWN))
            return@flow
        }

        for (category in ScanCategory.entries) {
            emit(ScanUpdate.CheckResult(SecurityCheck(category, CheckStatus.SCANNING)))
            delay(600L + Random.nextLong(0, 700L)) // 600–1 300 ms → 2.4–5.2 s total

            val status = resolveStatus(category)
            val check = SecurityCheck(category, status)
            completedChecks.add(check)

            score -= when (status) {
                CheckStatus.DANGER -> 25
                CheckStatus.WARNING -> 12
                CheckStatus.ERROR -> 5
                else -> 0
            }

            emit(ScanUpdate.CheckResult(check))
            delay(250L)
        }

        emit(ScanUpdate.Completed(buildResult(score.coerceAtLeast(0), completedChecks)))
    }

    // ── Status resolution ────────────────────────────────────────────────────

    private fun resolveStatus(category: ScanCategory): CheckStatus {
        if (Random.nextFloat() < 0.10f) return CheckStatus.ERROR // 10 % simulated error

        val roll = Random.nextFloat()
        return when (category) {
            ScanCategory.OS_VERSION -> if (roll < 0.70f) CheckStatus.SUCCESS else CheckStatus.WARNING
            ScanCategory.APP_THREATS -> when {
                roll < 0.60f -> CheckStatus.SUCCESS
                roll < 0.85f -> CheckStatus.WARNING
                else -> CheckStatus.DANGER
            }
            ScanCategory.WIFI_SAFETY -> if (roll < 0.65f) CheckStatus.SUCCESS else CheckStatus.WARNING
            ScanCategory.PASSWORD_STRENGTH -> when {
                roll < 0.50f -> CheckStatus.SUCCESS
                roll < 0.80f -> CheckStatus.WARNING
                else -> CheckStatus.DANGER
            }
        }
    }

    // ── Result assembly ──────────────────────────────────────────────────────

    private fun buildResult(score: Int, checks: List<SecurityCheck>): ScanResult {
        val risks = mutableListOf<RecommendationItem>()
        val recommendations = mutableListOf<RecommendationItem>()

        checks.forEach { check ->
            when (check.status) {
                CheckStatus.DANGER -> risks.addAll(risksFor(check.category))
                CheckStatus.WARNING -> recommendations.addAll(recommendationsFor(check.category))
                CheckStatus.ERROR -> recommendations.add(errorItem(check.category))
                else -> Unit
            }
        }

        return ScanResult(
            score = score,
            possibleRisks = risks,
            recommendations = recommendations,
            protectionUpgrades = protectionUpgrades()
        )
    }

    private fun risksFor(category: ScanCategory): List<RecommendationItem> = when (category) {
        ScanCategory.APP_THREATS -> listOf(
            RecommendationItem(
                id = "risk_malware",
                title = "Malicious App Detected",
                description = "One or more installed apps may be compromised. Remove them immediately.",
                icon = ItemIcon.VIRUS,
                actionLabel = "Fix Now"
            )
        )
        ScanCategory.PASSWORD_STRENGTH -> listOf(
            RecommendationItem(
                id = "risk_password",
                title = "Weak Passwords Found",
                description = "Critical accounts are using weak or reused passwords.",
                icon = ItemIcon.LOCK,
                actionLabel = "Fix Now"
            )
        )
        else -> emptyList()
    }

    private fun recommendationsFor(category: ScanCategory): List<RecommendationItem> = when (category) {
        ScanCategory.OS_VERSION -> listOf(
            RecommendationItem(
                id = "rec_os",
                title = "OS Update Available",
                description = "Your device is running an older OS version. Update to get the latest security patches.",
                icon = ItemIcon.OS_UPDATE,
                actionLabel = "Fix Now"
            )
        )
        ScanCategory.APP_THREATS -> listOf(
            RecommendationItem(
                id = "rec_apps",
                title = "Suspicious App Behaviour",
                description = "Some apps are requesting unusual permissions. Review and revoke unnecessary access.",
                icon = ItemIcon.VIRUS,
                actionLabel = "Fix Now"
            )
        )
        ScanCategory.WIFI_SAFETY -> listOf(
            RecommendationItem(
                id = "rec_wifi",
                title = "Unsecured Wi-Fi Network",
                description = "Your current network lacks WPA3 encryption. Avoid sensitive activities on this network.",
                icon = ItemIcon.WIFI,
                actionLabel = "Fix Now"
            )
        )
        ScanCategory.PASSWORD_STRENGTH -> listOf(
            RecommendationItem(
                id = "rec_password",
                title = "Improve Password Strength",
                description = "Several accounts use short or common passwords. Use unique, complex passwords.",
                icon = ItemIcon.LOCK,
                actionLabel = "Fix Now"
            )
        )
    }

    private fun errorItem(category: ScanCategory) = RecommendationItem(
        id = "err_${category.name.lowercase()}",
        title = "${category.displayName} Check Unavailable",
        description = "We couldn't complete this check. Retry the scan to get full results.",
        icon = ItemIcon.CLOUD,
        actionLabel = "Retry"
    )

    private fun protectionUpgrades() = listOf(
        RecommendationItem(
            id = "upgrade_vpn",
            title = "Enable VPN Protection",
            description = "Shield your browsing on public networks with Norton VPN.",
            icon = ItemIcon.VPN,
            actionLabel = "Set Up"
        ),
        RecommendationItem(
            id = "upgrade_password_manager",
            title = "Set Up Password Manager",
            description = "Store and autofill strong, unique passwords with Norton Password Manager.",
            icon = ItemIcon.PASSWORD,
            actionLabel = "Set Up"
        ),
        RecommendationItem(
            id = "upgrade_cloud",
            title = "Enable Cloud Backup",
            description = "Protect your important data with automatic encrypted cloud backup.",
            icon = ItemIcon.CLOUD,
            actionLabel = "Set Up"
        )
    )
}
