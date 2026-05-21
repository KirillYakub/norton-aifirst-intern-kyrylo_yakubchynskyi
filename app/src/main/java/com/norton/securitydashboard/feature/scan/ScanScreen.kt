package com.norton.securitydashboard.feature.scan

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norton.securitydashboard.ui.ObserveAsEvents
import com.norton.securitydashboard.domain.model.CheckStatus
import com.norton.securitydashboard.domain.model.RecommendationItem
import com.norton.securitydashboard.domain.model.ScanCategory
import com.norton.securitydashboard.domain.model.ScanResult
import com.norton.securitydashboard.domain.model.SecurityCheck
import com.norton.securitydashboard.feature.scan.components.CircularScoreIndicator
import com.norton.securitydashboard.ui.components.PrimaryActionButton
import com.norton.securitydashboard.feature.scan.components.RecommendationCard
import com.norton.securitydashboard.feature.scan.components.SecurityCheckItem
import com.norton.securitydashboard.ui.theme.SecurityHealthDashboardTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ── Root ─────────────────────────────────────────────────────────────────────

@Composable
fun ScanRoot(
    onNavigateBack: () -> Unit,
    viewModel: ScanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ScanEvent.NavigateBack -> onNavigateBack()
            is ScanEvent.ShowDeviceFixMessage -> {
                Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    ScanScreen(state = state, onAction = viewModel::onAction)
}

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    state: SmartScanState,
    onAction: (ScanAction) -> Unit
) {
    BackHandler(enabled = state.phase == ScanPhase.Scanning) {
        onAction(ScanAction.OnBackClick)
    }

    if (state.showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = { onAction(ScanAction.OnExitConfirmed) },
            onDismiss = { onAction(ScanAction.OnExitDismissed) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Scan") },
                navigationIcon = {
                    IconButton(onClick = { onAction(ScanAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val phase = state.phase) {
            ScanPhase.Idle, ScanPhase.Scanning -> {
                ScanningContent(
                    state = state,
                    modifier = Modifier.padding(padding)
                )
            }
            ScanPhase.Complete -> {
                ResultContent(
                    state = state,
                    onActionClick = { onAction(ScanAction.OnDeviceFixClick) },
                    modifier = Modifier.padding(padding)
                )
            }
            is ScanPhase.Failed -> {
                FailedContent(
                    message = phase.message,
                    onRetry = { onAction(ScanAction.OnRetry) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

// ── Scanning content ──────────────────────────────────────────────────────────

@Composable
private fun ScanningContent(
    state: SmartScanState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularScoreIndicator(
            progress = state.progress,
            score = state.displayedScore,
            isScanning = true
        )
        Spacer(Modifier.height(32.dp))
        state.checks.forEach { check ->
            SecurityCheckItem(check = check)
        }
    }
}

// ── Result content ────────────────────────────────────────────────────────────

@Composable
private fun ResultContent(
    state: SmartScanState,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = state.result ?: return
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabs = listOf("Possible Risks", "Recommendations", "Protection Upgrades")

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        CircularScoreIndicator(
            progress = state.progress,
            score = state.displayedScore,
            isScanning = false
        )
        Spacer(Modifier.height(24.dp))

        state.checks.forEach { check ->
            SecurityCheckItem(check = check)
        }

        Spacer(Modifier.height(16.dp))

        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val items = when (page) {
                0 -> result.possibleRisks
                1 -> result.recommendations
                else -> result.protectionUpgrades
            }
            RecommendationPage(
                items = items,
                onActionClick = onActionClick
            )
        }
    }
}

@Composable
private fun RecommendationPage(
    items: List<RecommendationItem>,
    onActionClick: () -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing to report here.\nYou're all good!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                RecommendationCard(item = item, onActionClick = onActionClick)
            }
        }
    }
}

// ── Failed content ────────────────────────────────────────────────────────────

@Composable
private fun FailedContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Scan failed",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(32.dp))
        PrimaryActionButton(text = "Retry", onClick = onRetry)
    }
}

// ── Dialog ────────────────────────────────────────────────────────────────────

@Composable
private fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Do you want to leave Smart Scan?") },
        text = { Text("The current scan will be cancelled.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Yes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("No") }
        }
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ScanScreenScanningPreview() {
    SecurityHealthDashboardTheme {
        ScanScreen(
            state = SmartScanState(
                phase = ScanPhase.Scanning,
                progress = 0.5f,
                checks = ScanCategory.entries.mapIndexed { i, cat ->
                    SecurityCheck(
                        category = cat,
                        status = when {
                            i == 0 -> CheckStatus.SUCCESS
                            i == 1 -> CheckStatus.SCANNING
                            else -> CheckStatus.PENDING
                        }
                    )
                }
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanScreenCompletePreview() {
    SecurityHealthDashboardTheme {
        ScanScreen(
            state = SmartScanState(
                phase = ScanPhase.Complete,
                progress = 1f,
                displayedScore = 72,
                checks = ScanCategory.entries.map { SecurityCheck(it, CheckStatus.SUCCESS) },
                result = ScanResult(
                    score = 72,
                    possibleRisks = emptyList(),
                    recommendations = emptyList(),
                    protectionUpgrades = emptyList()
                )
            ),
            onAction = {}
        )
    }
}
