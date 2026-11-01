
package com.example.taller2.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Repositorio encargado de gestionar la autenticación con Firebase.
class AuthRepository {

    // Obtener la instancia de Firebase Auth
    private fun obtenerAuth(): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    // Obtener el usuario autenticado
    fun obtenerUsuarioActual(): FirebaseUser? {
        return obtenerAuth()?.currentUser
    }

    // Iniciar sesión
    fun iniciarSesion(
        email: String,
        clave: String,
        onExito: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = obtenerAuth()

        if (auth == null) {
            onError("Firebase no está configurado.")
            return
        }

        auth.signInWithEmailAndPassword(email, clave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val usuario = auth.currentUser

                    if (usuario != null) {
                        onExito(usuario)
                    } else {
                        onError("No se pudo obtener el usuario.")
                    }
                } else {
                    onError(
                        task.exception?.localizedMessage
                            ?: "Error al iniciar sesión."
                    )
                }
            }
    }

    // Registrar un usuario
    fun registrarUsuario(
        email: String,
        clave: String,
        onExito: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = obtenerAuth()

        if (auth == null) {
            onError("Firebase no está configurado.")
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
                    onError(
                        task.exception?.localizedMessage
                            ?: "Error al crear la cuenta."
                    )
                }
            }
    }

    // Cerrar sesión
    fun cerrarSesion() {
        obtenerAuth()?.signOut()
    }
}
