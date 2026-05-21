package com.norton.securitydashboard.feature.scan

import com.norton.securitydashboard.domain.model.CheckStatus
import com.norton.securitydashboard.domain.model.ScanCategory
import com.norton.securitydashboard.domain.model.ScanError
import com.norton.securitydashboard.domain.model.ScanResult
import com.norton.securitydashboard.domain.model.ScanUpdate
import com.norton.securitydashboard.domain.model.SecurityCheck
import com.norton.securitydashboard.domain.repository.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 1. State transitions ──────────────────────────────────────────────────

    @Test
    fun `state transitions to Complete and result is stored when scan finishes`() = runTest {
        val result = sampleResult(score = 78)
        val viewModel = ScanViewModel(fakeRepoWith(ScanUpdate.Completed(result)))

        advanceUntilIdle()

        assertEquals(ScanPhase.Complete, viewModel.state.value.phase)
        assertEquals(result, viewModel.state.value.result)
        assertEquals(1f, viewModel.state.value.progress)
    }

    @Test
    fun `failed scan update transitions state to Failed phase`() = runTest {
        val viewModel = ScanViewModel(fakeRepoWith(ScanUpdate.Failed(ScanError.UNKNOWN)))

        advanceUntilIdle()

        assertTrue(viewModel.state.value.phase is ScanPhase.Failed)
    }

    // ── 2. Score calculation ──────────────────────────────────────────────────

    // AI-generated, reviewed and refined by me
    @Test
    fun `displayed score matches the score from the completed scan result`() = runTest {
        val expectedScore = 67
        val viewModel = ScanViewModel(fakeRepoWith(ScanUpdate.Completed(sampleResult(expectedScore))))

        advanceUntilIdle()

        assertEquals(expectedScore, viewModel.state.value.displayedScore)
    }

    // ── 3. Cancellation / back button behaviour ───────────────────────────────

    @Test
    fun `back click during scanning shows exit dialog without navigating`() = runTest {
        val viewModel = ScanViewModel(hangingRepo())
        advanceTimeBy(100L)

        viewModel.onAction(ScanAction.OnBackClick)

        assertTrue(viewModel.state.value.showExitDialog)
        assertEquals(ScanPhase.Scanning, viewModel.state.value.phase)
    }

    @Test
    fun `exit confirmed cancels scan and emits NavigateBack event`() = runTest {
        val viewModel = ScanViewModel(hangingRepo())
        val received = mutableListOf<ScanEvent>()
        val collector = launch { viewModel.events.collect { received.add(it) } }

        advanceTimeBy(100L)
        viewModel.onAction(ScanAction.OnExitConfirmed)
        advanceUntilIdle()

        assertTrue(ScanEvent.NavigateBack in received)
        collector.cancel()
    }

    @Test
    fun `back click after scan completes navigates back without showing dialog`() = runTest {
        val viewModel = ScanViewModel(fakeRepoWith(ScanUpdate.Completed(sampleResult(90))))
        val received = mutableListOf<ScanEvent>()
        val collector = launch { viewModel.events.collect { received.add(it) } }

        advanceUntilIdle()
        viewModel.onAction(ScanAction.OnBackClick)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.showExitDialog)
        assertTrue(ScanEvent.NavigateBack in received)
        collector.cancel()
    }

    // ── Fakes ─────────────────────────────────────────────────────────────────

    private fun fakeRepoWith(vararg updates: ScanUpdate) = object : ScanRepository {
        override fun startScan(): Flow<ScanUpdate> = flow {
            updates.forEach { emit(it) }
        }
    }

    /** Emits one SCANNING check and then hangs to simulate an in-progress scan. */
    private fun hangingRepo() = object : ScanRepository {
        override fun startScan(): Flow<ScanUpdate> = flow {
            emit(ScanUpdate.CheckResult(SecurityCheck(ScanCategory.OS_VERSION, CheckStatus.SCANNING)))
            delay(10_000L)
        }
    }

    private fun sampleResult(score: Int) = ScanResult(
        score = score,
        possibleRisks = emptyList(),
        recommendations = emptyList(),
        protectionUpgrades = emptyList()
    )
}
