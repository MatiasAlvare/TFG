@file:JvmName("LoginKt")
package com.example.tfg_matias.pantallas

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg_matias.R
import com.example.tfg_matias.utilidades.AuthRes
import com.example.tfg_matias.utilidades.AuthViewModel

@Composable
fun LoginScreen(
    onGoogleSignIn: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGuestAccess: () -> Unit = {}
) {

    val context = LocalContext.current
    val vm: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    val googleLauncher = rememberLauncherForActivityResult(
        StartActivityForResult()
    ) { result ->
        vm.handleGoogleResponse(result.data)
    }

    val authResult by vm.authResult.collectAsState()

    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var isPwVisible by remember { mutableStateOf(false) }
    var emailError  by remember { mutableStateOf(false) }
    var pwError     by remember { mutableStateOf(false) }

    val red    = Color(0xFFFF0000)
    val greyBg = Color(0xFFEEEEEE)

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Inicia sesión",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold, fontSize = 24.sp
            )
        )
        Spacer(Modifier.height(24.dp))

        // Google
        Button(
            onClick = {
                onGoogleSignIn()
                vm.email = email
                vm.password = password

                val client = vm.getGoogleClient(context as Activity)
                client.signOut().addOnCompleteListener {
                    googleLauncher.launch(client.signInIntent)
                }
            },
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape  = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = greyBg)
        ) {
            Icon(
                painterResource(R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint     = Color.Unspecified
            )
            Spacer(Modifier.width(8.dp))
            Text("Continuar con Google", color = Color.Black)
        }

        Spacer(Modifier.height(16.dp))

        // "o"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            HorizontalDivider(Modifier.weight(1f).height(1.dp), color = Color.Gray)
            Text("  o  ", style = TextStyle(color = Color.Gray, fontSize = 14.sp))
            HorizontalDivider(Modifier.weight(1f).height(1.dp), color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value        = email,
            onValueChange = {
                email = it
                if (emailError) emailError = false
            },
            label       = { Text("Email *") },
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.ic_gmail),
                    contentDescription = null,
                    Modifier.size(20.dp)
                )
            },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError) {
            Text(
                "Email inválido (debe contener @)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value            = password,
            onValueChange    = {
                password = it
                if (pwError) pwError = false
            },
            label            = { Text("Contraseña *") },
            leadingIcon      = {
                Icon(
                    painterResource(R.drawable.ic_candado),
                    contentDescription = null,
                    Modifier.size(20.dp)
                )
            },
            trailingIcon     = {
                IconButton(onClick = { isPwVisible = !isPwVisible }) {
                    Icon(
                        painterResource(
                            id = if (isPwVisible) R.drawable.ic_ojo_abierto else R.drawable.ic_ojo
                        ),
                        contentDescription = null,
                        Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isPwVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError    = pwError,
            modifier   = Modifier.fillMaxWidth()
        )
        if (pwError) {
            Text(
                "La contraseña no puede estar vacía",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(8.dp))

        // Forgot password aligned left
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿Has olvidado tu contraseña?",
                style = TextStyle(color = Color.Blue, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Sign in
        Button(
            onClick = {
                emailError = email.isBlank() || !email.contains("@")
                pwError    = password.isBlank()
                if (!emailError && !pwError) {
                    vm.email    = email
                    vm.password = password
                    vm.login()
                }
            },
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape  = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = red)
        ) {
            Text(
                "Inicia sesión",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Register box
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "¿Aun no tienes cuenta?",
                style = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Guest link (no box)
        Text(
            text = "Acceder sin cuenta",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onGuestAccess() }
                .padding(vertical = 8.dp),
            style = TextStyle(
                color = Color(0xFF0D47A1),               // mismo azul del enlace
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            ),
            textAlign = TextAlign.Center
        )
    }

    // Auth effect
    LaunchedEffect(authResult) {
        when (authResult) {
            is AuthRes.Success -> {
                vm.crearUsuarioSiNoExiste()  // ✅ AÑADIDO
                onLoginSuccess()
                vm.clearAuthResult()
            }

            is AuthRes.Error -> {
                Toast.makeText(
                    context,
                    (authResult as AuthRes.Error).errorMessage,
                    Toast.LENGTH_LONG
                ).show()
                vm.clearAuthResult()
            }
            else -> {}
        }
    }
}
