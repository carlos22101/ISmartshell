package com.carlos.ismartshell.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.*
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.core.managers.QrScannerManager
import com.carlos.ismartshell.features.auth.presentation.screens.LoginScreen
import com.carlos.ismartshell.features.auth.presentation.screens.RegisterScreen
import com.carlos.ismartshell.features.buyer.presentation.screens.*
import com.carlos.ismartshell.features.seller.presentation.screens.CreateStoreScreen
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

// ── Rutas ──────────────────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Register   : Screen("register")
    // Buyer tabs
    object HomeBuyer  : Screen("home_buyer")
    object QrScanner  : Screen("qr_scanner")
    object QrHistory  : Screen("qr_history")
    object StoreMap   : Screen("store_map/{businessId}") {
        fun createRoute(businessId: String) = "store_map/$businessId"
    }
    // Seller tabs
    object SellerHome : Screen("seller_home")
}

// ── BottomNav items ────────────────────────────────────────────────────────────
data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val buyerNavItems = listOf(
    BottomNavItem(Screen.HomeBuyer.route, "Tiendas",   Icons.Default.Store),
    BottomNavItem(Screen.QrScanner.route, "Escanear",  Icons.Default.QrCodeScanner),
    BottomNavItem(Screen.QrHistory.route, "Historial", Icons.Default.History)
)

val sellerNavItems = listOf(
    BottomNavItem(Screen.SellerHome.route, "Mis tiendas", Icons.Default.Store)
)

// ── NavHost principal ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    viewModel: AppNavViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val tokenManager = viewModel.tokenManager
    val isLoggedIn by tokenManager.isLoggedInFlow.collectAsStateWithLifecycle(false)
    val userRole   by tokenManager.userRoleFlow.collectAsStateWithLifecycle(null)
    val scope = rememberCoroutineScope()

    val startDest = when {
        !isLoggedIn       -> Screen.Login.route
        userRole == "seller" -> Screen.SellerHome.route
        else              -> Screen.HomeBuyer.route
    }

    val onLogout = {
        scope.launch {
            tokenManager.clearSession()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDest) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = if (role == "seller") Screen.SellerHome.route else Screen.HomeBuyer.route
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { role ->
                    val dest = if (role == "seller") Screen.SellerHome.route else Screen.HomeBuyer.route
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        // ── Buyer con BottomNav ──────────────────────────────────────────────
        composable(Screen.HomeBuyer.route) {
            BuyerScaffold(navController, onLogout) {
                HomeBuyerScreen(
                    onNavigateToMap = { id -> navController.navigate(Screen.StoreMap.createRoute(id)) }
                )
            }
        }

        composable(Screen.QrScanner.route) {
            val qrScannerManager: QrScannerManager = hiltViewModel<QrNavViewModel>().qrScannerManager
            BuyerScaffold(navController, onLogout) {
                QrScannerScreen(qrScannerManager = qrScannerManager)
            }
        }

        composable(Screen.QrHistory.route) {
            BuyerScaffold(navController, onLogout) { QrHistoryScreen() }
        }

        composable(
            route = Screen.StoreMap.route,
            arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) { backStack ->
            val businessId = backStack.arguments?.getString("businessId") ?: return@composable
            StoreMapScreen(businessId = businessId, onBack = { navController.popBackStack() })
        }

        // ── Seller ───────────────────────────────────────────────────────────
        composable(Screen.SellerHome.route) {
            val qrScannerManager: QrScannerManager = hiltViewModel<QrNavViewModel>().qrScannerManager
            SellerScaffold(navController, onLogout) {
                CreateStoreScreen(qrScannerManager = qrScannerManager)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuyerScaffold(
    navController: NavHostController,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("iSmartShell") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStack by navController.currentBackStackEntryAsState()
                val current = navBackStack?.destination?.route
                buyerNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick  = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.HomeBuyer.route) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        },
                        icon  = { Icon(item.icon, item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(Modifier.padding(padding)) { content() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellerScaffold(
    navController: NavHostController,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("iSmartShell (Vendedor)") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStack by navController.currentBackStackEntryAsState()
                val current = navBackStack?.destination?.route
                sellerNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick  = { navController.navigate(item.route) { launchSingleTop = true } },
                        icon  = { Icon(item.icon, item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(Modifier.padding(padding)) { content() }
    }
}

@HiltViewModel
class AppNavViewModel @Inject constructor(val tokenManager: TokenManager) : ViewModel()

@HiltViewModel
class QrNavViewModel @Inject constructor(val qrScannerManager: QrScannerManager) : ViewModel()
