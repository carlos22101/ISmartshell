package com.carlos.ismartshell.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.ui.components.AuthButton
import com.carlos.ismartshell.core.ui.components.AuthErrorBox
import com.carlos.ismartshell.core.ui.components.AuthTextField
import com.carlos.ismartshell.features.auth.presentation.viewmodels.LoginViewModel

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)
private val BrandPurple = Color(0xFF8B5CF6)

private val WaveShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.80f)
            cubicTo(size.width * 0.75f, size.height * 1.05f,
                size.width * 0.35f, size.height * 0.75f, 0f, size.height * 0.92f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onGoToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState   by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).role)
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // Hero navy con wave
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.48f).clip(WaveShape).background(BrandNavy),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)) {
                Box(Modifier.size(80.dp).clip(CircleShape).background(BrandOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Email, null, tint = BrandOrange, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("iSmartShell", color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold, fontSize = 30.sp,
                    style = MaterialTheme.typography.displaySmall)
                Spacer(Modifier.height(4.dp))
                Text("Tu marketplace inteligente",
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Card formulario
        Card(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).fillMaxHeight(0.60f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Text("Bienvenido de nuevo", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                Text("Ingresa tus credenciales para continuar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))

                AuthTextField(value = formState.email, onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = "Correo electrónico", icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email)
                Spacer(Modifier.height(14.dp))

                AuthTextField(value = formState.password, onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "Contraseña", icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password, isPassword = true,
                    passwordVisible = formState.passwordVisible,
                    onTogglePassword = { viewModel.onTogglePasswordVisible() })

                if (uiState is AuthUiState.Error) {
                    Spacer(Modifier.height(10.dp))
                    AuthErrorBox((uiState as AuthUiState.Error).message)
                }

                Spacer(Modifier.height(24.dp))

                AuthButton(text = "Iniciar sesión", onClick = { viewModel.login() },
                    isLoading = uiState is AuthUiState.Loading)

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text("  o  ", color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall)
                    HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onGoToRegister) {
                    Text("¿No tienes cuenta? ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium)
                    Text("Regístrate", color = BrandPurple, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}