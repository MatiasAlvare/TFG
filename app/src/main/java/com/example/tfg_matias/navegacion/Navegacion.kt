package com.example.tfg_matias.navegacion

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Home : NavRoutes("home") // <-- NUEVO
    object CarList : NavRoutes("car_list")
    object SellCar : NavRoutes("sell_car")
}