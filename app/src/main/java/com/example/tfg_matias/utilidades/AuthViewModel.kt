package com.example.tfg_matias.utilidades

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg_matias.Model.Usuario
import com.example.tfg_matias.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthRes {
    object Idle    : AuthRes()
    object Loading : AuthRes()
    object Success : AuthRes()
    data class Error(val errorMessage: String) : AuthRes()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _authResult = MutableStateFlow<AuthRes>(AuthRes.Idle)
    val authResult: StateFlow<AuthRes> = _authResult

    // Campos ligados a tus TextFields
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var nombre by mutableStateOf("")

    /**
     * Construye el Intent para iniciar el flujo de Google Sign-In
     * desde la Activity o Composable que lo invoque.
     */
    fun getGoogleSignInIntent(activity: Activity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                getApplication<Application>()
                    .getString(R.string.default_web_client_id)
            )
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(activity, gso)
        return googleClient.signInIntent
    }

    /** Procesa la respuesta de la Intent de Google */
    fun handleGoogleResponse(data: Intent?) = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        try {
            val account = GoogleSignIn
                .getSignedInAccountFromIntent(data)
                .await()
            val cred = GoogleAuthProvider
                .getCredential(account.idToken, null)
            Firebase.auth.signInWithCredential(cred).await()
            _authResult.value = AuthRes.Success
        } catch (e: Exception) {
            Log.e("AuthVM", "Google Sign-In failed:", e)
            _authResult.value = AuthRes.Error(e.localizedMessage ?: "Error Google Sign-In")
        }
    }

    /** Login con email+password */
    fun login() = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        try {
            Firebase.auth
                .signInWithEmailAndPassword(email, password)
                .await()
            _authResult.value = AuthRes.Success
        } catch (e: Exception) {
            Log.e("AuthVM", "Email login failed:", e)
            _authResult.value = AuthRes.Error(e.localizedMessage ?: "Error login")
        }
    }

    fun crearUsuarioSiNoExiste() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("users").document(user.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    val nuevoUsuario = hashMapOf(
                        "id"          to user.uid,
                        "email"       to (user.email ?: ""),
                        "name"        to (user.displayName ?: ""),
                        "photoUrl"    to (user.photoUrl?.toString() ?: ""),
                        "valoracion"  to 0.0,
                        "comentarios" to emptyList<String>()
                    )
                    userDoc.set(nuevoUsuario)
                }
            }
        }
    }


    fun register() = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        try {
            // 1) Registro en Firebase Auth
            Firebase.auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            // 2) Crear documento en Firestore
            Firebase.auth.currentUser?.let { fbUser ->
                val usuario = Usuario(
                    id       = fbUser.uid,
                    name     = nombre,                           // el campo “nombre” que ya capturas
                    photoUrl = fbUser.photoUrl?.toString() ?: "",
                    valoracion   = 0.0
                )
                Firebase
                    .firestore
                    .collection("users")
                    .document(fbUser.uid)
                    .set(usuario)
                    .await()
            }

            _authResult.value = AuthRes.Success
        } catch (e: Exception) {
            _authResult.value = AuthRes.Error(e.localizedMessage ?: "Error registro")
        }
    }

    /** Enviar email de reseteo */
    fun resetPassword() = viewModelScope.launch {
        _authResult.value = AuthRes.Loading
        try {
            Firebase.auth
                .sendPasswordResetEmail(email)
                .await()
            _authResult.value = AuthRes.Success
        } catch (e: Exception) {
            Log.e("AuthVM", "Reset password failed:", e)
            _authResult.value = AuthRes.Error(e.localizedMessage ?: "Error reset")
        }
    }

    /** Vuelve a Idle */
    fun clearAuthResult() {
        _authResult.value = AuthRes.Idle
    }
}
