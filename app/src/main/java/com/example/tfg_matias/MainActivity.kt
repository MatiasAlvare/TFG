package com.example.tfg_matias

import Resgistrarse
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tfg_matias.pantallas.HomeScreen
import com.example.tfg_matias.pantallas.LoginScreen
import com.example.tfg_matias.ui.theme.TFG_MATIASTheme
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TFG_MATIASTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Resgistrarse()
                    //LoginScreen()
                    HomeScreen()

                }
            }
        }
    }
}


sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Resgister : NavRoutes ("register")
    object Principal : NavRoutes("Principal")
    object Venta_car : NavRoutes("Venta_car")
    object Cerrar_Sesion : NavRoutes("Cerrar_Sesion")
}

@Composable
fun Navegacion(context : Context){
    val navController = rememberNavController()
}