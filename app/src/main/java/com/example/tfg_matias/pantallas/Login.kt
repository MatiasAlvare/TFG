@file:JvmName("LoginKt")

package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfg_matias.R

@Composable
fun LoginScreen(
    onGoogleSignIn: () -> Unit = {},
    onLoginClick: (email: String, password: String, keepSession: Boolean) -> Unit = { _, _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    // 1) Estados
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepSession by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Estados de error
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // 2) Colores ejemplo
    val primaryRed = Color(0xFFFF0000)
    val textColor = Color.Black
    val googleButtonBg = Color(0xFFEEEEEE)

    // 3) Layout principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Inicia sesión",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = textColor
        )

        Spacer(Modifier.height(24.dp))

        // 4) Botón "Continuar con Google"
        Button(
            onClick = onGoogleSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = googleButtonBg),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google), // tu icono
                contentDescription = "Google Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Continuar con Google", color = Color.Black)
        }

        // 5) Separador
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp),
                color = Color.Gray
            )
            Text(
                text = "  o  ",
                style = TextStyle(color = Color.Gray, fontSize = 14.sp)
            )
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp),
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(16.dp))

        // 6) Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it
                if (emailError) emailError = false},
            label = { Text("Email *") },

            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gmail),
                    contentDescription = "Icono gmail",
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError
        )

        Spacer(Modifier.height(8.dp))

        // 7) Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it
                if (passwordError) passwordError = false},
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_candado),
                    contentDescription = "Icono candado",
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.ic_ojo_abierto
                            else R.drawable.ic_ojo
                        ),
                        contentDescription = "Icono para mostrar/ocultar contraseña",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError,
        )
        if (passwordError) {
            Text(
                text = "La contraseña no puede estar vacía",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }


        Spacer(Modifier.height(8.dp))

        // 8) Checkbox "No cerrar sesión"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = keepSession,
                onCheckedChange = { keepSession = it }
            )
            Text(
                text = "No cerrar sesión",
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.clickable { keepSession = !keepSession }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 9) Botón de Iniciar sesión
        Button(
            onClick = {
                // Primero validamos si email o password están vacíos
                val isEmailEmpty = email.isBlank()
                val isPasswordEmpty = password.isBlank()

                emailError = isEmailEmpty
                passwordError = isPasswordEmpty

                // Si ninguno de los dos está vacío, llamamos a onLoginClick
                if (!emailError && !passwordError) {
                    onLoginClick(email, password, keepSession)
                }
                // Si algún campo está vacío, se marcarán los errores
                // y no llamamos a onLoginClick
            },
            colors = ButtonDefaults.buttonColors(containerColor = primaryRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Inicia sesión",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // 10) Botón de "¿Aún no tienes cuenta?"
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "¿Aún no tienes cuenta?",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // 11) Link "¿Has olvidado tu contraseña?"
        Text(
            text = "¿Has olvidado tu contraseña?",
            style = TextStyle(
                color = Color.Blue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .clickable { onForgotPasswordClick() }
                .padding(top = 8.dp)
        )
    }
}
