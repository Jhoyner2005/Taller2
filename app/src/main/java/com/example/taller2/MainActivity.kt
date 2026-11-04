
package com.example.taller2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taller2.Viewmodel.Authviewmodel
import com.example.taller2.ui.screens.Lobbyscreen
import com.example.taller2.ui.screens.Loginscreen
import com.example.taller2.ui.screens.CreateRoomScreen
import com.example.taller2.ui.screens.JoinRoomScreen
import com.example.taller2.ui.screens.RoomScreen
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
                    val navController = rememberNavController()

                    val estaAutenticado by
                    authViewModel.isAuthenticated.collectAsState()

                    val destinoInicial =
                        if (estaAutenticado) "lobby" else "login"

                    NavHost(
                        navController = navController,
                        startDestination = destinoInicial
                    ) {

                        // Pantalla de inicio de sesión
                        composable("login") {
                            Loginscreen(
                                authViewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("lobby") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        // Menú principal
                        composable("lobby") {
                            Lobbyscreen(
                                onNavigateToGame = {
                                    navController.navigate("createRoom")
                                },
                                onNavigateToJoin = {
                                    navController.navigate("joinRoom")
                                },
                                onCerrarSesion = {
                                    authViewModel.cerrarSesion()

                                    navController.navigate("login") {
                                        popUpTo("lobby") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        // Crear una partida
                        composable("createRoom") {
                            val userId = authViewModel.getCurrentUserId()

                            if (userId != null) {
                                CreateRoomScreen(
                                    userId = userId,
                                    userName = authViewModel.getCurrentUserName(),
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onRoomCreated = { roomCode ->
                                        navController.navigate("room/$roomCode")
                                    }
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No se pudo obtener el usuario.")
                                    Button(
                                        onClick = {
                                            navController.popBackStack()
                                        }
                                    ) {
                                        Text("Volver")
                                    }
                                }
                            }
                        }

                        // Unirse a una partida
                        composable("joinRoom") {
                            val userId = authViewModel.getCurrentUserId()

                            if (userId != null) {
                                JoinRoomScreen(
                                    userId = userId,
                                    userName = authViewModel.getCurrentUserName(),
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onRoomJoined = { roomCode ->
                                        navController.navigate("room/$roomCode")
                                    }
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No se pudo obtener el usuario.")
                                    Button(
                                        onClick = {
                                            navController.popBackStack()
                                        }
                                    ) {
                                        Text("Volver")
                                    }
                                }
                            }
                        }

                        // Sala de espera
                        composable(
                            route = "room/{roomCode}",
                            arguments = listOf(
                                navArgument("roomCode") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
                            val userId = authViewModel.getCurrentUserId() ?: ""

                            RoomScreen(
                                roomCode = roomCode,
                                userId = userId,
                                onBack = {
                                    navController.navigate("lobby") {
                                        popUpTo("lobby") {
                                            inclusive = false
                                        }
                                    }
                                },
                                onStartGame = { code ->
                                    navController.navigate("game/$code")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
