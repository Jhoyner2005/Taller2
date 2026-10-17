package com.example.taller2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Viewmodel.Authviewmodel

@Composable
fun Loginscreen(
    authViewModel: Authviewmodel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var esModoRegistro by remember { mutableStateOf(false) }

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val cargando by authViewModel.cargando.collectAsState()
    val mensajeError by authViewModel.mensajeError.collectAsState()

    // Navegar al lobby si se autentica correctamente
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DOMINÓ CAOS",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = if (esModoRegistro) "Crear Cuenta Nueva" else "Iniciar Sesión",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.limpiarError()
            },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.limpiarError()
            },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar mensaje de error si existe
        if (mensajeError != null) {
            Text(
                text = mensajeError ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Mostrar indicador de carga o el botón de acción
        if (cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (esModoRegistro) {
                        authViewModel.registrar(email, password)
                    } else {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (esModoRegistro) "Registrarse" else "Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    esModoRegistro = !esModoRegistro
                    authViewModel.limpiarError()
                }
            ) {
                Text(
                    if (esModoRegistro)
                        "¿Ya tienes cuenta? Inicia sesión aquí"
                    else
                        "¿No tienes cuenta? Regístrate aquí"
                )
            }
        }
    }
}
