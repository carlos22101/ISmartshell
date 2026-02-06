package com.carlos.ismartshell.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.carlos.ismartshell.core.di.AppViewModelProvider
import com.carlos.ismartshell.features.auth.presentation.screens.LoginScreen
import com.carlos.ismartshell.features.auth.presentation.screens.RegisterScreen
import com.carlos.ismartshell.features.auth.presentation.viewmodels.LoginViewModel
import com.carlos.ismartshell.features.buyer.presentation.screens.HomeBuyerScreen
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel
import com.carlos.ismartshell.features.seller.presentation.screens.CreateStoreScreen
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            val viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { role ->
                    if (role.equals("SELLER", ignoreCase = true)) {
                        navController.navigate("create_store") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home_buyer") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                // --- AQUÍ ESTÁ EL ARREGLO ---
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            val viewModel: com.carlos.ismartshell.features.auth.presentation.viewmodels.RegisterViewModel = viewModel(factory = AppViewModelProvider.Factory)
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home_buyer") {
            val viewModel: HomeBuyerViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeBuyerScreen(viewModel = viewModel)
        }

        composable("create_store") {
            val viewModel: CreateStoreViewModel = viewModel(factory = AppViewModelProvider.Factory)
            CreateStoreScreen(viewModel = viewModel)
        }
    }
}