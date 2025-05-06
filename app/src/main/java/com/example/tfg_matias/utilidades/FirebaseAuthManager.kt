package com.example.tfg_matias.utilidades

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.tfg_matias.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    /** → Intent para Google Sign-In */
    fun getGoogleSignInIntent(activity: Activity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso).signInIntent
    }

    /** → Procesa respuesta Google */
    fun handleGoogleSignInResult(
        data: Intent?,
        onResult: (Boolean, String?) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val acct = task.getResult(ApiException::class.java)!!
            val cred = GoogleAuthProvider.getCredential(acct.idToken, null)
            auth.signInWithCredential(cred)
                .addOnSuccessListener { res ->
                    val u = res.user!!
                    val info = mapOf(
                        "email"       to u.email,
                        "displayName" to u.displayName,
                        "photoUrl"    to (u.photoUrl?.toString() ?: ""),
                        "lastLogin"   to FieldValue.serverTimestamp()
                    )
                    db.collection("users")
                        .document(u.uid)
                        .set(info, SetOptions.merge())
                    onResult(true, null)
                }
                .addOnFailureListener { e ->
                    Log.e("AUTH_GOOGLE", "Google sign-in failed: ${e.message}")
                    onResult(false, e.message)
                }
        } catch (e: ApiException) {
            Log.e("AUTH_GOOGLE", "Google API exception: ${e.message}")
            onResult(false, e.message)
        }
    }

    /** → Registro email/password */
    fun registerWithEmail(
        email: String,
        password: String,
        nombre: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: ""
                db.collection("users")
                    .document(uid)
                    .set(mapOf("displayName" to nombre), SetOptions.merge())
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("AUTH_REGISTER", "Register failed: ${e.message}")
                onResult(false, e.message)
            }
    }

    /** → Login email/password */
    fun loginWithEmail(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("AUTH_LOGIN", "Login failed: ${e.message}")
                onResult(false, e.message)
            }
    }

    /** → **Restablecer contraseña** */
    fun sendPasswordResetEmail(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Log.i("AUTH_RESET", "✅ sendPasswordResetEmail: SUCCESS")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("AUTH_RESET", "❌ sendPasswordResetEmail: FAILED → ${e.message}")
                onResult(false, e.message)
            }
    }
}
