package com.carlos.ismartshell.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.carlos.ismartshell.features.maps.presentation.screens.StoreMapScreen
import com.carlos.ismartshell.features.qr_scanner.presentation.screens.QrHistoryScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)

sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Register   : Screen("register")
    object HomeBuyer  : Screen("home_buyer")
    object QrHistory  : Screen("qr_history")
    object SellerHome : Screen("seller_home")
    object StoreMap   : Screen("store_map/{businessId}") {
        fun createRoute(businessId: String) = "store_map/$businessId"
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val buyerNavItems = listOf(
    BottomNavItem(Screen.HomeBuyer.route, "Inicio",   Icons.Default.Home),
    BottomNavItem(Screen.QrHistory.route, "Pedidos",  Icons.Default.History)
)

val sellerNavItems = listOf(
    BottomNavItem(Screen.SellerHome.route, "Mis tiendas", Icons.Default.Store)
)

@Composable
fun AppNavHost(viewModel: AppNavViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val tokenManager  = viewModel.tokenManager
    val isLoggedIn by tokenManager.isLoggedInFlow.collectAsStateWithLifecycle(initialValue = null)
    val userRole   by tokenManager.userRoleFlow.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    val isSellerRole = remember(userRole) {
        val r = userRole?.lowercase()?.trim() ?: ""
        r == "seller" || r == "vendedor"
    }

    if (isLoggedIn == null || (isLoggedIn == true && userRole == null)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandOrange)
        }
        return
    }

    val startDest = if (isLoggedIn == true) {
        if (isSellerRole) Screen.SellerHome.route else Screen.HomeBuyer.route
    } else Screen.Login.route

    val onLogout: () -> Unit = {
        scope.launch {
            tokenManager.clearSession()
            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
        }
    }

    NavHost(navController = navController, startDestination = startDest) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = if (role.lowercase().trim().let { it == "seller" || it == "vendedor" })
                        Screen.SellerHome.route else Screen.HomeBuyer.route
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { role ->
                    val dest = if (role.lowercase().trim().let { it == "seller" || it == "vendedor" })
                        Screen.SellerHome.route else Screen.HomeBuyer.route
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.HomeBuyer.route) {
            BuyerScaffold(navController, onLogout) {
                HomeBuyerScreen(onNavigateToMap = { id ->
                    navController.navigate(Screen.StoreMap.createRoute(id))
                })
            }
        }

        composable(Screen.QrHistory.route) {
            BuyerScaffold(navController, onLogout) {
                QrHistoryScreen()
            }
        }

        composable(
            route = Screen.StoreMap.route,
            arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) { backStack ->
            val businessId = backStack.arguments?.getString("businessId") ?: return@composable
            StoreMapScreen(businessId = businessId, onBack = { navController.popBackStack() })
        }

        composable(Screen.SellerHome.route) {
            val qrScannerManager = hiltViewModel<QrNavViewModel>().qrScannerManager
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
                title = { Text("iSmartShell", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
                val navBackStack by navController.currentBackStackEntryAsState()
                val current = navBackStack?.destination?.route
                buyerNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.HomeBuyer.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandOrange,
                            selectedTextColor = BrandOrange,
                            indicatorColor = BrandOrange.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
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
                title = { Text("iSmartShell (Vendedor)", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
                val navBackStack by navController.currentBackStackEntryAsState()
                val current = navBackStack?.destination?.route
                sellerNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.SellerHome.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandOrange,
                            selectedTextColor = BrandOrange,
                            indicatorColor = BrandOrange.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}

@HiltViewModel
class AppNavViewModel @Inject constructor(val tokenManager: TokenManager) : ViewModel()

@HiltViewModel
class QrNavViewModel @Inject constructor(val qrScannerManager: QrScannerManager) : ViewModel()