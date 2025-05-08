// ✅ MainActivity.kt actualizado para usar Navegacion directamente

package com.example.tfg_matias

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.navegacion.Navegacion

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val carVM: CarViewModel = viewModel()

            // Carga inicial de coches
            LaunchedEffect(Unit) {
                carVM.loadCars()
            }

            // ✅ Aquí lanzamos la navegación general
            Navegacion(carVM = carVM)
        }
    }
}