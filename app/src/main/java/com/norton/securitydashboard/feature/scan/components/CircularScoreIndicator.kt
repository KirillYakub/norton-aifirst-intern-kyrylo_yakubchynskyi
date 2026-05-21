package com.norton.securitydashboard.feature.scan.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norton.securitydashboard.ui.theme.NortonYellow
import com.norton.securitydashboard.ui.theme.StatusDanger
import com.norton.securitydashboard.ui.theme.StatusSuccess
import com.norton.securitydashboard.ui.theme.StatusWarning

@Composable
fun CircularScoreIndicator(
    progress: Float,
    score: Int,
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isScanning) progress else score / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1500),
        label = "score"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val indicatorColor = when {
        isScanning -> NortonYellow
        score >= 80 -> StatusSuccess
        score >= 60 -> StatusWarning
        else -> StatusDanger
    }

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 10.dp,
            trackColor = trackColor,
            color = indicatorColor
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = animatedScore.toString(),
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = indicatorColor
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isScanning) "Scanning…" else scoreLabel(score),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun scoreLabel(score: Int) = when {
    score >= 80 -> "Good"
    score >= 60 -> "Fair"
    else -> "At Risk"
}
