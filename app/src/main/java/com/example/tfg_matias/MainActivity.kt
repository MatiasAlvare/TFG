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
import com.example.tfg_matias.ui.theme.TFG_MATIASTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        val chatId = extras?.getString("chatId")
        val cocheId = extras?.getString("cocheId")
        val sellerId = extras?.getString("sellerId")
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings

        setContent {
            val carVM: CarViewModel = viewModel()

            LaunchedEffect(Unit) {
                carVM.loadCars()
            }

            // Pasamos los extras al sistema de navegación
            Navegacion(
                carVM = carVM,
                initialChatId = chatId,
                initialCocheId = cocheId,
                initialSellerId = sellerId
            )

            TFG_MATIASTheme{ // aquí se aplica el tema personalizado
                Navegacion(
                    carVM = carVM,
                    initialChatId = chatId,
                    initialCocheId = cocheId,
                    initialSellerId = sellerId
                )
            }
        }
    }
}

