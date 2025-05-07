// AuthViewModel.kt
package com.example.tfg_matias.utilidades

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthRes {
    object Idle    : AuthRes()
    object Loading : AuthRes()
    object Success : AuthRes()
    data class Error(val errorMessage: String) : AuthRes()
}

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val webClientId = "TU_WEB_CLIENT_ID.apps.googleusercontent.com"
    private val manager     = FirebaseAuthManager(webClientId)

    // Estado general de auth (login/register/google)
    private val _authResult = MutableStateFlow<AuthRes>(AuthRes.Idle)
    val authResult: StateFlow<AuthRes> = _authResult

    // Estado de reset de contraseña
    private val _resetResult = MutableStateFlow<Boolean?>(null)
    val resetResult: StateFlow<Boolean?> = _resetResult

    // Campos vinculados a tus TextFields
    var email: String    = ""
    var password: String = ""
    var nombre: String   = ""

    // ─── Registro ────────────────────────────────────────────────────────────────
    fun register() = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        manager.registerWithEmail(email, password, nombre) { ok, err ->
            _authResult.value = if (ok) AuthRes.Success else AuthRes.Error(err ?: "Error registro")
        }
    }

    // ─── Login email/password ────────────────────────────────────────────────────
    fun login() = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        manager.loginWithEmail(email, password) { ok, err ->
            _authResult.value = if (ok) AuthRes.Success else AuthRes.Error(err ?: "Error login")
        }
    }

    // ─── Google Sign-In ──────────────────────────────────────────────────────────
    fun getGoogleSignInIntent(activity: Activity): Intent =
        manager.getGoogleSignInIntent(activity)

    fun handleGoogleResponse(data: Intent?) = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        manager.handleGoogleSignInResult(data) { ok, err ->
            _authResult.value = if (ok) AuthRes.Success else AuthRes.Error(err ?: "Error Google Sign-In")
        }
    }

    // ─── Reset de contraseña ─────────────────────────────────────────────────────
    /**
     * Lanza el envío de reset de contraseña y actualiza [_resetResult].
     */
    fun sendPasswordResetEmail(email: String) {
        // No corremos en coroutine: el Task lo maneja Firebase
        manager.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _resetResult.value = true
            }
            .addOnFailureListener { e ->
                _resetResult.value = false
            }
    }

    // ─── Limpieza de estados ─────────────────────────────────────────────────────
    fun clearAuthResult()  { _authResult.value  = AuthRes.Idle }
    fun clearResetResult() { _resetResult.value = null       }
}
