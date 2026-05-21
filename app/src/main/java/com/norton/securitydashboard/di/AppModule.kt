package com.norton.securitydashboard.di

import com.norton.securitydashboard.data.MockScanRepository
import com.norton.securitydashboard.domain.repository.ScanRepository
import com.norton.securitydashboard.feature.home.HomeViewModel
import com.norton.securitydashboard.feature.scan.ScanViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::MockScanRepository) { bind<ScanRepository>() }

    viewModelOf(::HomeViewModel)
    viewModelOf(::ScanViewModel)
}
