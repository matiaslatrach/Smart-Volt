# Smart-Volt — Dos faenas iniciales

Esta versión conserva el splash, el inicio de sesión simplificado, el GPS real y el Bluetooth de demostración.

## Faenas agregadas automáticamente

| Faena | Latitud | Longitud |
| --- | ---: | ---: |
| DUOC PADRE ALONSO DE OVALLE | -33.446327 | -70.657515 |
| PLAZA CONSTITUCION | -33.441754 | -70.654102 |

Ambas se crean como **activas**, con ubicación **Santiago**, al arrancar la aplicación.

- La creación es **idempotente**: no se vuelve a insertar una faena con el mismo nombre (ignorando mayúsculas/minúsculas).
- No se cambia el nombre, la ubicación ni las coordenadas de una faena que ya exista con ese nombre.
- **No se inventan fechas de inicio ni término:** quedan vacías. El administrador puede completarlas al editar cada faena.
- No se asignan automáticamente operarios a ninguna faena: así no se modifica la asignación existente. Para probar la entrada, entra como administrador, asigna el operario a una de las dos faenas y comprueba sus coordenadas.
- Si ya hay otras faenas registradas, se conservan; las dos propuestas se agregan solo si faltan.
- Si borras manualmente una de estas faenas, se volverá a crear en el siguiente inicio de la aplicación. Este comportamiento es intencional para la demostración.

## Archivos de esta modificación

- `app/src/main/java/com/example/smartvolt/datos/local/FaenasIniciales.kt`: datos iniciales.
- `app/src/main/java/com/example/smartvolt/datos/local/dao/FaenaDAO.kt`: inserción SQL atómica sin duplicados.
- `app/src/main/java/com/example/smartvolt/MainActivity.kt`: llamada al iniciar en hilo de E/S.

## Prueba rápida

1. Abrir este proyecto en Android Studio y sincronizar Gradle.
2. Ejecutar en un teléfono Android.
3. Iniciar sesión con una cuenta de administrador y abrir **Gestión de faenas**.
4. Comprobar que aparecen las dos faenas con las coordenadas indicadas.
5. Cerrar y abrir la aplicación: el listado no debe duplicarse.
6. Asignar un operario a una de las dos faenas para probar el registro de entrada.

**Nota:** La compilación completa y las pruebas en un dispositivo deben hacerse en Android Studio. No se modificó el repositorio de GitHub.
