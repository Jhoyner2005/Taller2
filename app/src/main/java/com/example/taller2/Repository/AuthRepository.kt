package com.example.taller2.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Repositorio encargado de gestionar la autenticación y registro con Firebase Auth.
class AuthRepository {

    // Obtener instancia de Firebase Auth de forma segura para evitar cierres si falta google-services.json
    private fun obtenerAuth(): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    // Retorna el usuario actualmente autenticado o null si no hay ninguno.
    fun obtenerUsuarioActual(): FirebaseUser? {
        return obtenerAuth()?.currentUser
    }

    // Inicia sesión con correo electrónico y contraseña.
    fun iniciarSesion(
        email: String,
        clave: String,
        onExito: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = obtenerAuth()
        if (auth == null) {
            onError("Firebase no está configurado. Falta el archivo google-services.json.")
            return
        }

        auth.signInWithEmailAndPassword(email, clave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val usuario = auth.currentUser
                    if (usuario != null) {
                        onExito(usuario)
                    } else {
                        onError("No se pudo obtener la información del usuario.")
                    }
                } else {
                    val mensajeError = task.exception?.localizedMessage ?: "Error al iniciar sesión."
                    onError(mensajeError)
                }
            }
    }

    // Registra un nuevo usuario con correo electrónico y contraseña.
    fun registrarUsuario(
        email: String,
        clave: String,
        onExito: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = obtenerAuth()
        if (auth == null) {
            onError("Firebase no está configurado. Falta el archivo google-services.json.")
            return
        }

        auth.createUserWithEmailAndPassword(email, clave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val usuario = auth.currentUser
                    if (usuario != null) {
                        onExito(usuario)
                    } else {
                        onError("No se pudo registrar el usuario.")
                    }
                } else {
                    val mensajeError = task.exception?.localizedMessage ?: "Error al crear la cuenta."
                    onError(mensajeError)
                }
            }
    }

    // Cierra la sesión activa.
    fun cerrarSesion() {
        obtenerAuth()?.signOut()
    }
}