package com.norton.securitydashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.norton.securitydashboard.feature.home.HomeRoot
import com.norton.securitydashboard.feature.scan.ScanRoot

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeRoot(onNavigateToScan = { navController.navigate(SmartScanRoute) })
        }
        composable<SmartScanRoute> {
            ScanRoot(onNavigateBack = { navController.navigateUp() })
        }
    }
}
