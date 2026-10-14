package com.example.taller2.Viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Authviewmodel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun login(email: String, contrasena: String) {
        if (email.isNotEmpty() && contrasena.isNotEmpty()) {
            _isAuthenticated.value = true
        }
    }
}