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
    object Success : AuthRes()
    data class Error(val errorMessage: String?) : AuthRes()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val manager      = FirebaseAuthManager()
    private val _authResult  = MutableStateFlow<AuthRes?>(null)
    val authResult: StateFlow<AuthRes?> = _authResult

    private val _resetResult = MutableStateFlow<Boolean?>(null)
    val resetResult: StateFlow<Boolean?> = _resetResult

    var email: String    = ""
    var password: String = ""
    var nombre: String   = ""

    fun register() = viewModelScope.launch {
        manager.registerWithEmail(email, password, nombre) { ok, err ->
            _authResult.value = if (ok) AuthRes.Success else AuthRes.Error(err)
        }
    }

    fun login() = viewModelScope.launch {
        manager.loginWithEmail(email, password) { ok, err ->
            _authResult.value = if (ok) AuthRes.Success else AuthRes.Error(err)
        }
    }

    fun sendPasswordResetEmail(trim: String) = viewModelScope.launch {
        manager.sendPasswordResetEmail(email) { ok, err ->
            _resetResult.value = ok
            if (!ok) {
                // Ver en Logcat desde el VM
                android.util.Log.e("VM_RESET", "Reset failed: $err")
            }
        }
    }

    fun clearAuthResult()  { _authResult.value  = null }
    fun clearResetResult() { _resetResult.value = null }

    fun getGoogleSignInIntent(activity: Activity): Intent =
        manager.getGoogleSignInIntent(activity)

    fun handleGoogleResponse(data: Intent?) = viewModelScope.launch {
        manager.handleGoogleSignInResult(data) { ok, err ->
            _authResult.value = if (ok) AuthRes.Success else AuthRes.Error(err)
        }
    }
}
