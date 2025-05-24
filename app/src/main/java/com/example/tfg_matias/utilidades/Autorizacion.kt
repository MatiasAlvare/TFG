package com.example.tfg_matias.utilidades

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

/**
 * Muestra un diálogo pidiendo al usuario que inicie sesión o se registre.
 */
@Composable
fun Autorizacion(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* no cerrar al hacer clic fuera */ },
        title = { Text("Acceso requerido") },
        text = { Text("Debes registrarte o iniciar sesión para continuar.") },
        confirmButton = {
            Button(onClick = onRegister) {
                Text("Regístrate")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onLogin) {
                Text("Inicia sesión")
            }
        }
    )
}

/**
 * Composable que comprueba si hay usuario logueado.
 * Si no, lanza AuthPrompt; si sí, muestra el contenido.
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
