package com.example.taller2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taller2.Viewmodel.Authviewmodel
import com.example.taller2.ui.screens.Lobbyscreen
import com.example.taller2.ui.screens.Loginscreen
import com.example.taller2.ui.theme.Taller2Theme

class MainActivity : ComponentActivity() {
    private val authViewModel: Authviewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Taller2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controlador de navegación de Jetpack Compose
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            Loginscreen(
                                authViewModel = authViewModel,
                                onLoginSuccess = {
                                    // Navega al lobby y evita que el usuario vuelva al login presionando "Atrás"
                                    navController.navigate("lobby") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("lobby") {
                            Lobbyscreen(
                                onNavigateToGame = {
                                    // Más adelante, aquí añadirás la navegación hacia GameScreen
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}