@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tfg_matias.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.tfg_matias.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OlvidoContraseña(
    navController: NavController
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val primaryRed = Color(0xFFFF0000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Restablece tu contraseña",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (emailError) emailError = false
            },
            label = { Text("Email *") },
            leadingIcon = {
                Icon(
                    painter           = painterResource(id = R.drawable.ic_gmail),
                    contentDescription = null,
                    modifier          = Modifier.size(20.dp)   // tamaño original
                )
            },
            isError = emailError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier   = Modifier.fillMaxWidth()
        )
        if (emailError) {
            Text(
                text  = "Introduce un email válido",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (loading) return@Button
                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = true
                    Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email.trim())
                    .addOnSuccessListener {
                        loading = false
                        Toast.makeText(context, "Enlace enviado", Toast.LENGTH_LONG).show()
                        navController.navigate("reset_confirmation") {
                            popUpTo("forgot_password") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        Toast.makeText(
                            context,
                            "Error enviando enlace: ${e.localizedMessage ?: "desconocido"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            },
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryRed),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = if (loading) "Enviando..." else "Enviar enlace", color = Color.White)
        }
    }
}
