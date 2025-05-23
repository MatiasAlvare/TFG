@file:JvmName("RegisterKt")
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
import androidx.navigation.NavController
import com.example.tfg_matias.R
import com.example.tfg_matias.utilidades.AuthRes
import com.example.tfg_matias.utilidades.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registrarse(
    onGoogleSignIn: () -> Unit = {},
    navController: NavController,
    onRegisterCompleted: () -> Unit = {},
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


    // ——————————————————————————————
    // Campos de formulario y flags de error
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    // ——————————————————————————————

    val primaryRed = Color(0xFFFF0000)
    val greyBg     = Color(0xFFEEEEEE)

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Registrarse",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp
            )
        )
        Spacer(Modifier.height(24.dp))

        // — Google Sign-In Button —
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

        // — Divider con "o" —
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Divider(Modifier.weight(1f).height(1.dp), color = Color.Gray)
            Text("  o  ", style = TextStyle(color = Color.Gray, fontSize = 14.sp))
            Divider(Modifier.weight(1f).height(1.dp), color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        // — Nombre —
        OutlinedTextField(
            value         = nombre,
            onValueChange = {
                nombre = it
                if (nombreError) nombreError = false
            },
            label       = { Text("Nombre *") },
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_usuario),
                    contentDescription = "Nombre",
                    modifier = Modifier.size(20.dp)
                )
            },
            isError  = nombreError,
            modifier = Modifier.fillMaxWidth()
        )
        if (nombreError) {
            Text(
                "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(8.dp))

        // — Email —
        OutlinedTextField(
            value         = email,
            onValueChange = {
                email = it
                if (emailError) emailError = false
            },
            label       = { Text("Email *") },
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_gmail),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            },
            isError  = emailError,
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

        // — Contraseña —
        OutlinedTextField(
            value                = password,
            onValueChange        = {
                password = it
                if (passwordError) passwordError = false
            },
            label                = { Text("Contraseña *") },
            leadingIcon          = {
                Icon(
                    painterResource(id = R.drawable.ic_candado),
                    contentDescription = "Contraseña",
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon         = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painterResource(
                            id = if (isPasswordVisible) R.drawable.ic_ojo_abierto
                            else                 R.drawable.ic_ojo
                        ),
                        contentDescription = "Mostrar/ocultar",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            isError              = passwordError,
            modifier             = Modifier.fillMaxWidth()
        )
        if (passwordError) {
            Text(
                "La contraseña no puede estar vacía",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(16.dp))

        // — Botón Registrarse —
        Button(
            onClick = {
                // validaciones
                nombreError   = nombre.isBlank()
                emailError    = email.isBlank() || !email.contains("@")
                passwordError = password.isBlank()
                if (!nombreError && !emailError && !passwordError) {
                    vm.nombre   = nombre
                    vm.email    = email
                    vm.password = password
                    vm.register()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape  = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
        ) {
            Text(
                "Registrarse",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(8.dp))

        // — ¿Ya tienes cuenta? —
        OutlinedButton(
            onClick  = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "¿Ya tienes cuenta?",
                style = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(8.dp))

        // — Acceder sin cuenta —
        Text(
            text      = "Acceder sin cuenta",
            modifier  = Modifier
                .fillMaxWidth()
                .clickable { onGuestAccess() }
                .padding(vertical = 8.dp),
            style     = TextStyle(
                color          = Color(0xFF0D47A1),
                fontSize       = 14.sp,
                fontWeight     = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            ),
            textAlign = TextAlign.Center
        )
    }

    // — Navegación según resultado de registro —
    LaunchedEffect(authResult) {
        when (authResult) {
            is AuthRes.Success -> {
                vm.crearUsuarioSiNoExiste()  // ✅ AÑADIDO
                onRegisterCompleted()
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
            else -> { /* Idle / Loading */ }
        }
    }
}
