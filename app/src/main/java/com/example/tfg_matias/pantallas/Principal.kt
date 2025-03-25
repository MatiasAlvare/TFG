package com.example.tfg_matias.pantallas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfg_matias.R

@Composable
fun Principal(
    userName: String = "Usuario",
    onHomeClick: () -> Unit = {},
    onPublishCarClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSearchChange: (String) -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    // Estado para el texto de búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Simulamos una lista de coches
    val carList = remember {
        mutableStateListOf(
            CarData(
                title = "SEAT Ibiza 1.0 EcoTSI Style",
                price = "12.990 €",
                infoLine = "Gasolina • 2019 • 95.000 km • 95 cv • Barcelona",
                warranty = "Garantía 1 año | IVA incluido",
                imageRes = R.drawable.ibiza
            ),
            CarData(
                title = "Ford Focus 1.5 TDCi Trend",
                price = "10.500 €",
                infoLine = "Diesel • 2018 • 120.000 km • 110 cv • Madrid",
                warranty = "Garantía 1 año",
                imageRes = R.drawable.ford_focus
            )
            // Añade más si quieres...
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo CARFLOW (ocupa toda la pantalla con algo de opacidad)
        Image(
            painter = painterResource(id = R.drawable.fonodo_carflow),
            contentDescription = "CarFlow Background",
            modifier = Modifier.fillMaxSize(),
            alpha = 0.07f
        )

        // Contenido principal
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // -- Parte superior --
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Nombre grande arriba
                Text(
                    text = "CARFLOW",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Círculo central superior con el icono de usuario
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    // Icono de usuario
                    Icon(
                        painter = painterResource(id = R.drawable.usuario),
                        contentDescription = "Icono Usuario",
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de búsqueda y botón de filtrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo de búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            onSearchChange(it)
                        },
                        label = { Text("Buscar...") },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón de filtrar
                    IconButton(
                        onClick = onFilterClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filtrar),
                            contentDescription = "Filtrar"
                        )
                    }
                }
            }

            // -- Parte central: listado de coches --
            // Filtramos la lista según la búsqueda
            val filteredCars = carList.filter { car ->
                car.title.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCars) { carData ->
                    CarItemCard(carData)
                }
            }

            // -- Parte inferior: fila con círculos --
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo izquierda (ir a pantalla principal)
                CircleIconButton(
                    iconId = R.drawable.ic_coche,
                    description = "Ir al inicio",
                    onClick = onHomeClick
                )

                // Círculo centro (publicar coche)
                CircleIconButton(
                    iconId = R.drawable.ic_vender_coche,
                    description = "Publicar coche",
                    onClick = onPublishCarClick
                )

                // Círculo derecha (cerrar sesión)
                CircleIconButton(
                    iconId = R.drawable.ic_cerrar_sesion,
                    description = "Cerrar sesión",
                    onClick = onLogoutClick
                )
            }
        }
    }
}

// Tarjeta de cada coche
@Composable
fun CarItemCard(carData: CarData) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            // Imagen (tamaño fijo de ejemplo: 200dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = carData.imageRes),
                    contentDescription = "Car image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Info del coche
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = carData.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = carData.price,
                    color = Color(0xFFD32F2F), // Rojo
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = carData.warranty,
                    color = Color.Green,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = carData.infoLine,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Modelo de datos para cada coche
data class CarData(
    val title: String,
    val price: String,
    val infoLine: String,
    val warranty: String,
    val imageRes: Int
)

// Botón circular de la parte inferior
@Composable
fun CircleIconButton(
    iconId: Int,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = description,
            modifier = Modifier.size(24.dp)
        )
    }
}
