package com.example.taller2.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.taller2.Repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Authviewmodel : ViewModel() {

    private val repository = AuthRepository()

    // Estado para saber si el usuario está autenticado
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Estado para saber si se está procesando (cargando)
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Estado para mostrar mensajes de error en la UI
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    init {
        // Verificar si ya hay un usuario logueado al iniciar la app
        if (repository.obtenerUsuarioActual() != null) {
            _isAuthenticated.value = true
        }
    }

    // Iniciar sesión con email y contraseña
    fun login(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Por favor completa todos los campos."
            return
        }

        _cargando.value = true
        _mensajeError.value = null

        repository.iniciarSesion(
            email = email,
            clave = contrasena,
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

    // Registrar un nuevo usuario con email y contraseña
    fun registrar(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Por favor completa todos los campos."
            return
        }

        if (contrasena.length < 6) {
            _mensajeError.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        _cargando.value = true
        _mensajeError.value = null

        repository.registrarUsuario(
            email = email,
            clave = contrasena,
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

    // Limpiar el mensaje de error de la pantalla
    fun limpiarError() {
        _mensajeError.value = null
    }

    // Cerrar la sesión
    fun cerrarSesion() {
        repository.cerrarSesion()
        _isAuthenticated.value = false
    }
}