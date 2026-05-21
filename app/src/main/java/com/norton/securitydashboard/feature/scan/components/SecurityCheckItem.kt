package com.norton.securitydashboard.feature.scan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.norton.securitydashboard.domain.model.ScanCategory
import com.norton.securitydashboard.domain.model.SecurityCheck
import com.norton.securitydashboard.ui.theme.StatusPending

@Composable
fun SecurityCheckItem(check: SecurityCheck, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconFor(check.category),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = StatusPending
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = check.category.displayName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        StatusBadge(status = check.status)
    }
}

private fun iconFor(category: ScanCategory): ImageVector = when (category) {
    ScanCategory.OS_VERSION -> Icons.Default.PhoneAndroid
    ScanCategory.APP_THREATS -> Icons.Default.BugReport
    ScanCategory.WIFI_SAFETY -> Icons.Default.Wifi
    ScanCategory.PASSWORD_STRENGTH -> Icons.Default.Lock
}
