# Splash animado de Smart-Volt

## Qué cambió

- Nuevo `app/src/main/java/com/example/smartvolt/ui/view/splash/SplashSmartVoltView.kt`.
- `MainActivity.kt` muestra el splash al iniciar y después deja entrar a `NavegacionPrincipal` sin modificar sus rutas ni el login.
- Animación de aparición del logotipo (rayo), marca SmartVolt, progreso y desvanecimiento final (~2,1 segundos).
- `values/themes.xml`, `values-v31/themes.xml` y `values/colors.xml` usan el verde oscuro de Smart-Volt durante el arranque nativo para reducir el destello blanco antes de Compose.

## Cómo probar

1. Abrir la carpeta `Smart-Volt-Master-Demo-Bluetooth-GPS` en Android Studio.
2. Sincronizar Gradle y ejecutar **Build > Make Project**.
3. Ejecutar en teléfono/emulador. Al arrancar aparece el splash y luego el login.
4. Girar el teléfono si quieres verificar que no se repita el splash tras recreación de la actividad.
5. Cerrar la aplicación completamente y volver a abrir para ver la animación de nuevo.

## Notas

- Se conserva la validación GPS + Bluetooth simulado del paquete corregido anterior.
- El splash es visual; **no** interpreta la barra de progreso como una comprobación real de inicialización.
- No se añaden permisos, dependencias externas ni cambios de Room.
- No se pudo ejecutar una compilación completa de Android aquí por falta del Android SDK. El resultado necesita la comprobación de Android Studio.
