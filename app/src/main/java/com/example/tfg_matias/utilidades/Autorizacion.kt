package com.example.tfg_matias.utilidades

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

/**
 * Muestra un diálogo pidiendo al usuario que inicie sesión o se registre.
 */
@Composable
fun RequireAuth(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    if (isLoggedIn) {
        content()
    }
}
