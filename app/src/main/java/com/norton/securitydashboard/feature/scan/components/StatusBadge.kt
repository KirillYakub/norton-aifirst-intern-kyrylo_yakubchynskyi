package com.norton.securitydashboard.feature.scan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norton.securitydashboard.domain.model.CheckStatus
import com.norton.securitydashboard.ui.theme.StatusDanger
import com.norton.securitydashboard.ui.theme.StatusError
import com.norton.securitydashboard.ui.theme.StatusPending
import com.norton.securitydashboard.ui.theme.StatusScanning
import com.norton.securitydashboard.ui.theme.StatusSuccess
import com.norton.securitydashboard.ui.theme.StatusWarning

@Composable
fun StatusBadge(status: CheckStatus, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        CheckStatus.PENDING -> "Pending" to StatusPending
        CheckStatus.SCANNING -> "Scanning" to StatusScanning
        CheckStatus.SUCCESS -> "Safe" to StatusSuccess
        CheckStatus.WARNING -> "Warning" to StatusWarning
        CheckStatus.DANGER -> "Danger" to StatusDanger
        CheckStatus.ERROR -> "Error" to StatusError
    }

    Box(
        modifier = modifier
            .background(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}
