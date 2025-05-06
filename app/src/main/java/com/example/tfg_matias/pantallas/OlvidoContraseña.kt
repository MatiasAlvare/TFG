@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tfg_matias.pantallas

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Colores según tu tema
    val primaryRed = Color(0xFFFF0000)
    val textColor  = Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Restablece tu contraseña",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize   = 24.sp,
                color      = textColor,
                fontWeight = FontWeight.Bold
            )
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
                    contentDescription = "Icono email",
                    modifier          = Modifier.size(20.dp)
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
                Log.i("PWD_RESET", ">>> Botón pulsado con email=$email")
                if (loading) return@Button

                if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = true
                    Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email.trim())
                    .addOnSuccessListener {
                        Log.i("PWD_RESET", "✅ Éxito enviando reset a $email")
                        loading = false
                        Toast.makeText(context, "Enlace enviado", Toast.LENGTH_LONG).show()
                        navController.navigate("reset_confirmation") {
                            popUpTo("forgot_password") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PWD_RESET", "❌ Fallo enviando reset: ${e?.message}")
                        loading = false
                        Toast.makeText(context, "Error enviando enlace", Toast.LENGTH_LONG).show()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape  = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryRed),
            enabled = !loading
        ) {
            Text(
                text  = if (loading) "Enviando..." else "Enviar enlace",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text  = "¿Aún no tienes cuenta?",
                style = TextStyle(
                    color      = textColor,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
