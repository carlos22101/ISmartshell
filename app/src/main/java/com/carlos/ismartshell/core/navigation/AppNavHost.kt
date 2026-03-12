package com.carlos.ismartshell.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.carlos.ismartshell.features.auth.presentation.screens.LoginScreen
import com.carlos.ismartshell.features.auth.presentation.screens.RegisterScreen
import com.carlos.ismartshell.features.buyer.presentation.screens.HomeBuyerScreen
import com.carlos.ismartshell.features.seller.presentation.screens.CreateStoreScreen
import com.carlos.ismartshell.features.buyer.presentation.screens.QrScannerScreen
import com.carlos.ismartshell.features.buyer.presentation.screens.StoreMapScreen
import com.carlos.ismartshell.features.buyer.presentation.screens.QrHistoryScreen
import com.carlos.ismartshell.features.auth.presentation.viewmodels.LoginViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    val role = viewModel.uiState.user?.role ?: ""
                    Log.d("DEBUG_ROL", "Rol obtenido del VM: '$role'")
                    val dest = if (role.equals("SELLER", ignoreCase = true)) "create_store" else "home_buyer"
                    navController.navigate(dest) { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = hiltViewModel(),
                onRegisterSuccess = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("home_buyer") {
            HomeBuyerScreen(
                viewModel = hiltViewModel(),
                onNavigateToQr = { navController.navigate("qr_scanner") },
                onNavigateToMap = { storeId -> navController.navigate("store_map/$storeId") },
                onNavigateToHistory = { navController.navigate("qr_history") }
            )
        }

        composable("create_store") {
            CreateStoreScreen(viewModel = hiltViewModel())
        }

        composable("qr_scanner") {
            QrScannerScreen(
                viewModel = hiltViewModel(),
                onQrResult = { value ->
                    Log.d("QR", "Código escaneado: $value")
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate("qr_history") }
            )
        }

        composable("qr_history") {
            QrHistoryScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        composable("store_map/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toIntOrNull() ?: -1
            StoreMapScreen(
                storeId = storeId,
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
