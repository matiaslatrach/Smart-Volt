# Smart-Volt · Refactor seguro de `master`

**Base:** rama `master`, commit `48c9ea8f7870f07fc8ca5d24283b694a636fd69b`.

## Qué cambió

- Colores de las pantallas existentes reutilizan la paleta central de `ui/theme/Color.kt`.
- Se centralizan tres colores semánticos compartidos (`TextoHeaderClaro`, `FondoExito` y `FondoError`) para preservar los tonos originales.
- Nuevo componente `ui/components/CampoTextoSmartVolt.kt` que concentra el estilo de los campos editables de gestión.
- `smartVoltTextFieldColors()` concentra los colores de los formularios (incluido el login); el texto escrito tiene contraste oscuro sobre el fondo blanco incluso con modo oscuro del sistema.
- Gestión de faenas: cinco campos repetidos pasan al componente reutilizable, manteniendo valores, textos, modo de varias líneas y validación.
- Gestión de usuarios: se elimina `CampoUsuario` duplicado y se reutiliza el componente común para los formularios y contraseña.

## Qué NO cambió

- Sin cambios en `MainActivity`, ViewModels, navegación, entidades, DAO, repositorios, migraciones, permisos, `build.gradle.kts`, lógica de asistencia ni roles.
- No incorpora las funciones exclusivas de la rama `bluetooth` (GPS, BLE simulado, solicitudes de excepción, Room v7).
- No se modificó el repositorio remoto de GitHub; el acceso de escritura a la integración devolvió HTTP 403.

## Cómo abrir

1. Descomprimir este ZIP.
2. Abrir **Smart-Volt-Master-Refactor** desde Android Studio.
3. Ejecutar Gradle Sync y **Build > Make Project**.
4. Validar inicio de sesión, gestión de faenas y usuarios en un emulador/dispositivo.

**Validación:** integridad del ZIP, estructura y comprobaciones estáticas de cambios. El proyecto **no fue compilado ni ejecutado** por falta del Android SDK en este entorno.
