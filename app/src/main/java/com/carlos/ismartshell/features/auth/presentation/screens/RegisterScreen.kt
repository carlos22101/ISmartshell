package com.carlos.ismartshell.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.auth.presentation.viewmodels.RegisterViewModel

private val BrandOrange = Color(0xFFF97316)
private val BrandPurple = Color(0xFF8B5CF6)

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

    Column(Modifier.fillMaxSize().background(Color(0xFFFFF9EE))
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp).padding(top = 48.dp, bottom = 40.dp)) {

        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
        Spacer(Modifier.height(4.dp))
        Text("Únete a iSmartShell", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
        Spacer(Modifier.height(28.dp))

        RegisterField(value = formState.name, onValueChange = { viewModel.onNameChange(it) },
            placeholder = "Nombre completo", icon = Icons.Default.Person, isActive = formState.name.isNotEmpty())
        Spacer(Modifier.height(12.dp))

        RegisterField(value = formState.email, onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "Correo electrónico", icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email, isActive = formState.email.isNotEmpty())
        Spacer(Modifier.height(12.dp))

        RegisterField(value = formState.password, onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = "Contraseña", icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password, isActive = formState.password.isNotEmpty(),
            isPassword = true, passwordVisible = formState.passwordVisible,
            onTogglePassword = { viewModel.onTogglePasswordVisible() })
        Spacer(Modifier.height(24.dp))

        Text("Selecciona tu rol", style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold, color = Color(0xFF1E1B13))
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoleCard(selected = !formState.isSeller, onClick = { viewModel.onRoleChange(false) },
                icon = Icons.Default.ShoppingBag, title = "Comprador", subtitle = "Buscar productos",
                selectedColor = BrandOrange, selectedBg = Color(0xFFFFF3E0), modifier = Modifier.weight(1f))
            RoleCard(selected = formState.isSeller, onClick = { viewModel.onRoleChange(true) },
                icon = Icons.Default.Store, title = "Vendedor", subtitle = "Vender productos",
                selectedColor = BrandPurple, selectedBg = Color(0xFFF3E8FF), modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(28.dp))

        if (uiState is AuthUiState.Error) {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null,
                    tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text((uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(16.dp))
        }

        Button(onClick = { viewModel.register() },
            enabled = uiState !is AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange,
                disabledContainerColor = BrandOrange.copy(alpha = 0.5f)),
            elevation = ButtonDefaults.buttonElevation(4.dp)) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
            } else {
                Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("¿Ya tienes cuenta? ", color = Color(0xFF6B7280), fontSize = 14.sp)
            Text("Inicia sesión", color = BrandPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                modifier = Modifier.clickable { onGoToLogin() })
        }
    }
}

@Composable
private fun RegisterField(
    value: String, onValueChange: (String) -> Unit, placeholder: String, icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text, isActive: Boolean = false,
    isPassword: Boolean = false, passwordVisible: Boolean = false, onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF9CA3AF)) },
        leadingIcon = {
            Icon(icon, null, tint = if (isActive) BrandOrange else Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
        },
        trailingIcon = if (isPassword) ({
            IconButton(onClick = { onTogglePassword?.invoke() }) {
                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
            }
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandOrange,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color(0xFFFFF7F0), unfocusedContainerColor = Color.White))
}

@Composable
private fun RoleCard(
    selected: Boolean, onClick: () -> Unit, icon: ImageVector, title: String, subtitle: String,
    selectedColor: Color, selectedBg: Color, modifier: Modifier = Modifier
) {
    val borderColor = if (selected) selectedColor else Color(0xFFE5E7EB)
    val bgColor     = if (selected) selectedBg   else Color.White
    val iconTint    = if (selected) selectedColor else Color(0xFF9CA3AF)
    val titleColor  = if (selected) selectedColor else Color(0xFF1E1B13)
    val borderWidth = if (selected) 2.dp else 1.dp

    Column(modifier.clip(RoundedCornerShape(14.dp)).background(bgColor)
        .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
        .clickable { onClick() }.padding(14.dp), horizontalAlignment = Alignment.Start) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = titleColor)
        Text(subtitle, fontSize = 11.sp, color = Color(0xFF6B7280))
    }
}