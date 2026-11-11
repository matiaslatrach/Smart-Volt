# Smart-Volt — Prototipo integrado (Bluetooth simulado + GPS real)

Esta carpeta es el **proyecto Android completo** basado en el ZIP original aportado por el usuario, con el prototipo integrado. No requiere `instalar.py`.

## Ejecutar

1. Abrir **esta carpeta** con Android Studio (contiene `settings.gradle.kts`).
2. Sincronizar Gradle y ejecutar **Build > Make Project**.
3. Usar un emulador Android con servicios de ubicación o un teléfono Android.
4. Iniciar sesión como administrador; abrir **Faenas** y establecer coordenadas válidas para una faena; asignar esa faena a un operario.
5. Iniciar sesión con el operario; abrir **Asistencia**. Aceptar permiso de ubicación precisa. Activar **Simular baliza detectada**; validar Bluetooth de demostración y GPS real (100 m).
6. Si falla la validación, se puede solicitar una excepción; cerrar sesión y entrar como supervisor **en el mismo dispositivo** para aprobarla desde **Solicitudes**.
7. Revisar **Historial** para ver las jornadas agrupadas.

## Límites importantes

- **No es una aplicación ya compilada**: este entorno no tiene Android SDK y no se pudo verificar la compilación ni ejecutar pruebas en un dispositivo.
- Bluetooth es **simulado**, no se realiza escaneo real de una baliza. Los registros ordinarios de este prototipo se marcan `DEMO_BLE` y no deben tratarse como asistencia laboral certificada.
- La validación GPS rechaza localizaciones etiquetadas como simuladas (`Location.isMock` / `isFromMockProvider`), así como precisión insuficiente o posición fuera de 100 m. Esto **no garantiza** detectar toda falsificación, especialmente en dispositivos alterados.
- Las solicitudes al supervisor se guardan en Room **en una sola instalación**: no se sincronizan entre teléfonos.
- Las migraciones 5→7 y 6→7 fueron preparadas para preservar datos, pero deben probarse con bases reales antes de usar en producción. El código elimina el fallback destructivo.
- No se modificó GitHub; todo está contenido en este ZIP.

## Principales archivos modificados

- `app/src/main/java/com/example/smartvolt/servicios/ValidacionAsistencia.kt` (GPS + antisimulación + BLE demo)
- `app/src/main/java/com/example/smartvolt/ui/view/operario/AsistenciaOperarioView.kt`
- `app/src/main/java/com/example/smartvolt/ui/view/operario/HistorialOperarioView.kt`
- `app/src/main/java/com/example/smartvolt/ui/view/supervisor/SolicitudesSupervisorView.kt`
- `app/src/main/java/com/example/smartvolt/datos/local/AppDataBase.kt`
- `app/src/main/java/com/example/smartvolt/modelo/Asistencia.kt`
- `app/src/main/java/com/example/smartvolt/modelo/Faena.kt`

## Sobre accesos de prueba

El proyecto original crea usuarios de demostración al iniciar. **No uses estas credenciales de prueba en producción.**
