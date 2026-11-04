
package com.example.taller2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Viewmodel.Authviewmodel

private val BackgroundColor = Color(0xFF11152B)
private val CardColor = Color(0xFF1D2340)
private val AccentColor = Color(0xFF9B83FF)
private val SecondaryText = Color(0xFFB7BCD2)

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

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DOMINÓ CAOS",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The strategy is in your hands",
            fontSize = 14.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(20.dp))

        DominoDecoration()

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (esModoRegistro) "Create account" else "Welcome!",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (esModoRegistro)
                        "Sign up to start playing"
                    else
                        "Log in and demonstrate your strategy",
                    fontSize = 13.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        authViewModel.limpiarError()
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = SecondaryText,
                        focusedLabelColor = AccentColor,
                        unfocusedLabelColor = SecondaryText,
                        cursorColor = AccentColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        authViewModel.limpiarError()
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = SecondaryText,
                        focusedLabelColor = AccentColor,
                        unfocusedLabelColor = SecondaryText,
                        cursorColor = AccentColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (mensajeError != null) {
                    Text(
                        text = mensajeError ?: "",
                        color = Color(0xFFFF7777),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (cargando) {
                    CircularProgressIndicator(
                        color = AccentColor
                    )
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
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (esModoRegistro)
                                "Create account"
                            else
                                "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            esModoRegistro = !esModoRegistro
                            authViewModel.limpiarError()
                        }
                    ) {
                        Text(
                            text = if (esModoRegistro)
                                "Already have an account? Log in"
                            else
                                "Don't have an account? Sign up",
                            color = AccentColor,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "DOMINÓ CAOS • Play with strategy",
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun DominoDecoration() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DominoPiece(firstValue = 1, secondValue = 3)
        DominoPiece(firstValue = 2, secondValue = 5)
        DominoPiece(firstValue = 4, secondValue = 6)
    }
}

@Composable
private fun DominoPiece(
    firstValue: Int,
    secondValue: Int
) {
    Surface(
        modifier = Modifier.size(width = 54.dp, height = 76.dp),
        shape = RoundedCornerShape(9.dp),
        color = Color(0xFFECEBFA),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "●".repeat(firstValue),
                fontSize = 9.sp,
                color = Color(0xFF292344),
                maxLines = 1
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 5.dp),
                color = Color(0xFF77718E),
                thickness = 1.dp
            )

            Text(
                text = "●".repeat(secondValue),
                fontSize = 9.sp,
                color = Color(0xFF292344),
                maxLines = 1
            )
        }
    }
}
