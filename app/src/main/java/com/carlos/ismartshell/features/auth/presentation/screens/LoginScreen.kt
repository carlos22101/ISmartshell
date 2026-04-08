package com.carlos.ismartshell.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.auth.presentation.viewmodels.LoginViewModel

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)
private val BrandPurple = Color(0xFF8B5CF6)
private val WarmWhite   = Color(0xFFFFF9EE)

private val WaveShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.80f)
            cubicTo(size.width * 0.75f, size.height * 1.05f, size.width * 0.35f, size.height * 0.75f, 0f, size.height * 0.92f)
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

    Box(modifier = Modifier.fillMaxSize().background(WarmWhite)) {

        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.48f).clip(WaveShape).background(BrandNavy),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 32.dp)) {
                Box(Modifier.size(80.dp).clip(CircleShape).background(BrandOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Email, null, tint = BrandOrange, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("iSmartShell", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                Spacer(Modifier.height(4.dp))
                Text("Tu marketplace inteligente", color = Color.White.copy(alpha = 0.65f), fontSize = 14.sp)
            }
        }

        Card(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).fillMaxHeight(0.60f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Text("Bienvenido de nuevo", fontWeight = FontWeight.Bold, fontSize = 22.sp,
                    color = Color(0xFF1E1B13), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                Text("Ingresa tus credenciales para continuar", fontSize = 13.sp,
                    color = Color(0xFF6B7280), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = { Text("Correo electrónico", color = Color(0xFF9CA3AF)) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, null,
                            tint = if (formState.email.isNotEmpty()) BrandOrange else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color(0xFFFFF7F0), unfocusedContainerColor = Color(0xFFF9FAFB))
                )
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = formState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = { Text("Contraseña", color = Color(0xFF9CA3AF)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null,
                            tint = if (formState.password.isNotEmpty()) BrandOrange else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onTogglePasswordVisible() }) {
                            Icon(if (formState.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                        }
                    },
                    visualTransformation = if (formState.passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color(0xFFFFF7F0), unfocusedContainerColor = Color(0xFFF9FAFB))
                )

                if (uiState is AuthUiState.Error) {
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEE2E2)).padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text((uiState as AuthUiState.Error).message, color = Color(0xFF991B1B), fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(onClick = { viewModel.login() },
                    enabled = uiState !is AuthUiState.Loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange,
                        disabledContainerColor = BrandOrange.copy(alpha = 0.5f)),
                    elevation = ButtonDefaults.buttonElevation(4.dp)) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                    } else {
                        Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE5E7EB))
                    Text("  o  ", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE5E7EB))
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onGoToRegister) {
                    Text("¿No tienes cuenta? ", color = Color(0xFF6B7280), fontSize = 14.sp)
                    Text("Regístrate", color = BrandPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}