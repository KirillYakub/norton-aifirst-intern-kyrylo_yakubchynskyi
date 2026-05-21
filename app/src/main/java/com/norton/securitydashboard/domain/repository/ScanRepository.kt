package com.norton.securitydashboard.domain.repository

import com.norton.securitydashboard.domain.model.ScanUpdate
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
    fun startScan(): Flow<ScanUpdate>
}
