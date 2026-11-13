# Corrección de referencias en Gestión de faenas

Se agregaron dos importaciones de Jetpack Compose que faltaban en `GestionFaenasView.kt`:

```kotlin
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
```

Estas funciones son necesarias para el formulario desplazable. No se cambiaron las validaciones de GPS/Bluetooth ni el esquema de Room.

## Abrir

Abre la carpeta de proyecto en Android Studio, sincroniza Gradle y ejecuta `Build > Make Project`. El proyecto no pudo compilarse con el SDK de Android en este entorno; si hay otro error, copia la primera línea roja del panel Build Output.
