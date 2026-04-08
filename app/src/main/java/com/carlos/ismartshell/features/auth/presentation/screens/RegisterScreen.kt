package com.carlos.ismartshell.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.carlos.ismartshell.core.ui.components.RoleCard
import com.carlos.ismartshell.features.auth.presentation.viewmodels.RegisterViewModel

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)
private val BrandPurple = Color(0xFF8B5CF6)

private val WaveShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.75f)
            cubicTo(size.width * 0.75f, size.height * 1.0f,
                size.width * 0.35f, size.height * 0.70f, 0f, size.height * 0.88f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: (role: String) -> Unit,
    onGoToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState   by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess((uiState as AuthUiState.Success).role)
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // Hero navy con wave (más pequeño que login)
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.28f).clip(WaveShape).background(BrandNavy),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)) {
                Box(Modifier.size(56.dp).clip(CircleShape).background(BrandOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PersonAdd, null, tint = BrandOrange, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text("Crear cuenta", color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge)
                Text("Únete a iSmartShell",
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall)
            }
        }

        // Card formulario ocupa resto de pantalla
        Card(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).fillMaxHeight(0.76f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 28.dp, bottom = 32.dp)
            ) {
                AuthTextField(value = formState.name, onValueChange = { viewModel.onNameChange(it) },
                    placeholder = "Nombre completo", icon = Icons.Default.Person)
                Spacer(Modifier.height(12.dp))

                AuthTextField(value = formState.email, onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = "Correo electrónico", icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email)
                Spacer(Modifier.height(12.dp))

                AuthTextField(value = formState.password, onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "Contraseña", icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password, isPassword = true,
                    passwordVisible = formState.passwordVisible,
                    onTogglePassword = { viewModel.onTogglePasswordVisible() })

                Spacer(Modifier.height(20.dp))

                Text("Selecciona tu rol", style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RoleCard(selected = !formState.isSeller, onClick = { viewModel.onRoleChange(false) },
                        icon = Icons.Default.ShoppingBag, title = "Comprador",
                        subtitle = "Buscar productos", selectedColor = BrandOrange,
                        selectedBg = Color(0xFFFFF3E0), modifier = Modifier.weight(1f))
                    RoleCard(selected = formState.isSeller, onClick = { viewModel.onRoleChange(true) },
                        icon = Icons.Default.Store, title = "Vendedor",
                        subtitle = "Vender productos", selectedColor = BrandPurple,
                        selectedBg = Color(0xFFF3E8FF), modifier = Modifier.weight(1f))
                }

                if (uiState is AuthUiState.Error) {
                    Spacer(Modifier.height(16.dp))
                    AuthErrorBox((uiState as AuthUiState.Error).message)
                }

                Spacer(Modifier.height(24.dp))

                AuthButton(text = "Registrarse", onClick = { viewModel.register() },
                    isLoading = uiState is AuthUiState.Loading)

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("¿Ya tienes cuenta? ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium)
                    Text("Inicia sesión", color = BrandPurple, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onGoToLogin() })
                }
            }
        }
    }
}