# CarFlow 

---

**CarFlow** es una aplicación móvil desarrollada como Trabajo de Fin de Grado. La app permite a los usuarios comprar y vender coches de segunda mano, chatear con vendedores y gestionar su perfil. El proyecto está desarrollado con **Android Studio** usando **Kotlin**, **Jetpack Compose** y servicios de **Firebase** (Auth, Firestore, Storage y FCM).

---

## Descripción

En **CarFlow**, los usuarios pueden publicar anuncios de coches, explorar vehículos disponibles, aplicar filtros avanzados (marca, año, precio, provincia, etiqueta ambiental, etc.), contactar con vendedores por chat y valorar a otros usuarios. 

---

## Características

- **Framework principal**: Android Studio + Jetpack Compose
- **Backend**: Firebase (Firestore, Auth, Storage, Messaging)
- **Autenticación**: Google y email/password
- **Notificaciones Push**: mensajes en tiempo real usando Firebase Cloud Messaging
- **Valoraciones**: sistema de comentarios con estrellas
- **Filtros inteligentes**: búsqueda avanzada con múltiples criterios
- **Galería de imágenes**: vista interactiva de fotos 
- **Modo edición**: permite editar y eliminar publicaciones
- **Diseño UI**: inspirado en apps de compraventa de coches reales

---

## Estructura del Proyecto

```
/pantallas
    - Pantallas principales (Busqueda.kt, Vender.kt, Detalle.kt, ChatPantalla.kt, etc.)
/ViewModel
    - ViewModels principales (CarViewModel.kt, ChatViewModel.kt)
/Model
    - Modelos de datos (Coche.kt, Usuario.kt, Comentario.kt)
/utilidades
    - Servicios auxiliares (FirebaseMessagingService.kt)
/navegacion
    - Navegación de la app (Navegacion.kt)
MainActivity.kt
```

---

## Requisitos del Sistema

- Android Studio Arctic Fox o superior
- Emulador Android o dispositivo real
- Conexión a internet
- Archivo `google-services.json` válido ya incluido en el proyecto

---

## Cómo Ejecutar la Aplicación

1. **Clona el repositorio** en tu máquina local:
   ```bash
   git clone https://github.com/MatiasAlvare/TFG.git
   ```

2. **Abre Android Studio** y selecciona la carpeta del proyecto.

3. Espera a que gradle sincronice y asegúrate de tener una conexión a internet activa para Firebase.

4. **Ejecuta la aplicación** pulsando el botón "Run" (o Mayús + F10).

5. Accede con Google o email y empieza a explorar los coches publicados.

---

## Recursos Utilizados

- Firebase Auth, Firestore, Storage, Messaging
- Jetpack Compose + Material3
- Coil (carga de imágenes)
- OkHttp + Gson (envío manual de notificaciones FCM)

---

## Licencia

Este proyecto se distribuye bajo la licencia **MIT**.

---

## Contacto

**Autor**: Matías Álvarez  
**GitHub**: [https://github.com/MatiasAlvare](https://github.com/MatiasAlvare)
