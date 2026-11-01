
package com.example.taller2.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.taller2.Repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Authviewmodel : ViewModel() {

    private val repository = AuthRepository()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    init {
        _isAuthenticated.value =
            repository.obtenerUsuarioActual() != null
    }

    // Iniciar sesión
    fun login(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Por favor completa todos los campos."
            return
        }

        _cargando.value = true
        _mensajeError.value = null

        repository.iniciarSesion(
            email,
            contrasena,
            onExito = {
                _cargando.value = false
                _isAuthenticated.value = true
            },
            onError = { error ->
                _cargando.value = false
                _mensajeError.value = error
            }
        )
    }

    // Registrar usuario
    fun registrar(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Por favor completa todos los campos."
            return
        }

        if (contrasena.length < 6) {
            _mensajeError.value =
                "La contraseña debe tener al menos 6 caracteres."
            return
        }

        _cargando.value = true
        _mensajeError.value = null

        repository.registrarUsuario(
            email,
            contrasena,
            onExito = {
                _cargando.value = false
                _isAuthenticated.value = true
            },
            onError = { error ->
                _cargando.value = false
                _mensajeError.value = error
            }
        )
    }

    // Obtener el ID del usuario actual
    fun getCurrentUserId(): String? {
        return repository.obtenerUsuarioActual()?.uid
    }

    // Obtener el nombre del usuario actual
    fun getCurrentUserName(): String {
        val user = repository.obtenerUsuarioActual()

        return user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email
            ?: "Player"
    }

    // Limpiar mensaje de error
    fun limpiarError() {
        _mensajeError.value = null
    }

    // Cerrar sesión
    fun cerrarSesion() {
        repository.cerrarSesion()
        _isAuthenticated.value = false
    }
}
