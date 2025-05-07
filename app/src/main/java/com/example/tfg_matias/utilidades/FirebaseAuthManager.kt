// FirebaseAuthManager.kt
package com.example.tfg_matias.utilidades

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val webClientId: String) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /** Construye y devuelve el Intent de Google Sign-In */
    fun getGoogleSignInIntent(activity: Activity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso).signInIntent
    }

    /** Maneja el resultado de la pantalla de Google y hace sign-in en Firebase */
    suspend fun handleGoogleSignInResult(data: Intent?, callback: (Boolean, String?) -> Unit) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
            val cred    = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(cred).await()
            callback(true, null)
        } catch (e: Exception) {
            callback(false, e.localizedMessage)
        }
    }

    /** Registro con email/password */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        nombre: String,
        callback: (Boolean, String?) -> Unit
    ) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            callback(true, null)
        } catch (e: Exception) {
            callback(false, e.localizedMessage)
        }
    }

    /** Login con email/password */
    suspend fun loginWithEmail(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            callback(true, null)
        } catch (e: Exception) {
            callback(false, e.localizedMessage)
        }
    }

    /** Envia el correo de restablecimiento de contraseña */
    fun sendPasswordResetEmail(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }
}
