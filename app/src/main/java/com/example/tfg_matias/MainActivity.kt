package com.example.tfg_matias

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.navegacion.Navegacion

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        val chatId = extras?.getString("chatId")
        val cocheId = extras?.getString("cocheId")
        val sellerId = extras?.getString("sellerId")

        setContent {
            val carVM: CarViewModel = viewModel()

            LaunchedEffect(Unit) {
                carVM.loadCars()
            }

            // ✅ Pasamos los extras al sistema de navegación
            Navegacion(
                carVM = carVM,
                initialChatId = chatId,
                initialCocheId = cocheId,
                initialSellerId = sellerId
            )
        }
    }
}

