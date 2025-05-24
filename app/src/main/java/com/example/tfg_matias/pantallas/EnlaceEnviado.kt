// Composable que muestra una pantalla de confirmación tras enviar un email de recuperación
package com.example.tfg_matias.pantallas

// Importaciones necesarias para UI y estilos
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnlaceEnviado(
    onContinue: () -> Unit // Acción a ejecutar al pulsar el botón "Continuar"
) {
    // Definición del color rojo principal y color del texto
    val primaryRed = Color(0xFFFF0000)
    val textColor  = Color.Black

    // Contenedor principal en columna, centrado en pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement  = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text      = "Tienes un email",
            style     = MaterialTheme.typography.headlineSmall.copy(
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color     = textColor,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp)) // Espaciado vertical

        // Mensaje informativo con instrucciones
        Text(
            text      = "Te hemos enviado un enlace con el que restablecer tu contraseña.\n\n" +
                    "Puede tardar unos minutos en llegarte. Si no lo recibes, revisa tu carpeta de spam para asegurarte de que no haya terminado allí.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = textColor,
            textAlign = TextAlign.Center,
            modifier  = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Botón para continuar
        Button(
            onClick   = onContinue,
            modifier  = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape     = RoundedCornerShape(8.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = primaryRed)
        ) {
            Text(
                text      = "Continuar",
                style     = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color     = Color.White
            )
        }
    }
}
