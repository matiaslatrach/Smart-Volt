# Smart-Volt — Entrada validada con GPS real y baliza Bluetooth simulada

Proyecto Android Studio basado en **`master`** (`48c9ea8`, no en la rama `bluetooth`), usando la versión refactorizada del diseño.

## Qué hace

- **Activar / comprobar Bluetooth:** solicita el permiso `BLUETOOTH_CONNECT` en Android 12+ y muestra la confirmación del sistema para encender Bluetooth si está apagado. Necesita Bluetooth real del teléfono.
- **Simular baliza detectada:** interruptor de demostración. **No hay detección BLE de una baliza física**. No funciona si Bluetooth está apagado.
- **Activar / comprobar ubicación:** pide ubicación **precisa**, comprueba que el GPS del teléfono está encendido y obtiene una lectura nueva.
- El GPS rechaza posiciones simuladas identificadas por Android, lecturas anteriores a 15 segundos, precisión superior a 30 metros y ubicaciones cuya **distancia + incertidumbre GPS exceda 100 metros** de la faena.
- La **entrada** se habilita cuando ambas validaciones están listas y vuelve a comprobarse el GPS **al presionar Registrar entrada**. La capa DAO rechaza entradas sin la validación declarada por el flujo y detecta si el estado de la jornada cambió mientras se marcaba.
- La **salida** conserva el comportamiento original (no exige estas validaciones en este prototipo).
- Las entradas se guardan con `metodoRegistro = DEMO_BLE_GPS` y se identifican como demostración en asistencia e historial.

**Seguridad:** esto es una demo para pruebas académicas. El interruptor simula una baliza, por lo que NO acredita presencia mediante Bluetooth. `Location.isMock` tampoco detecta todos los tipos de suplantación GPS. No usar como sistema certificado de asistencia.

## Configuración

1. Extrae el ZIP y abre la carpeta `Smart-Volt-Master-Demo-Bluetooth-GPS` en Android Studio mediante **Open**.
2. Sincroniza Gradle y ejecuta **Build > Make Project**.
3. Inicia sesión como **administrador**. En **Gestión de faenas**, crea o edita la faena y guarda su **latitud y longitud reales** (ej. `-33.4500`, `-70.6500` es solo un ejemplo, no la ubicación de tu faena). Asegúrate de que esté activa y asignada al operario.
4. Ejecuta la app en un **teléfono Android físico** que tenga Bluetooth y GPS. Se recomienda probar al aire libre por precisión.
5. Inicia sesión como operario y abre **Control de asistencia**.
6. Pulsa **Activar / comprobar Bluetooth**. Acepta el permiso de dispositivos cercanos y la activación si el sistema lo pide.
7. Activa **Simular baliza detectada** (modo demo).
8. Pulsa **Activar / comprobar ubicación**, concede acceso **preciso** y, si es necesario, activa GPS desde Ajustes. Vuelve a pulsar el botón para conseguir una lectura.
9. Si cumples los requisitos, pulsa **Registrar entrada**. El sistema vuelve a verificar una ubicación reciente antes de guardar.
10. Comprueba la entrada y su etiqueta de demostración en **Historial**. La siguiente marcación será **Registrar salida**.

**Importante:** si usas un emulador y le inyectas una posición de prueba, Android normalmente la marcará como ubicación simulada; el proyecto **la rechazará intencionalmente**. Para probar la ruta exitosa de GPS real utiliza un teléfono físico cerca de las coordenadas de la faena.

## Organización del código

- `servicios/ValidacionEntrada.kt`: comprobación Bluetooth y validación GPS (permisos, frescura, precisión, ubicación simulada y distancia).
- `servicios/ReglasEntrada.kt`: reglas simples reutilizables con tests JUnit.
- `ui/view/operario/ValidacionEntradaView.kt`: controles y mensajes de la entrada.
- `ui/view/operario/AsistenciaOperarioView.kt`: integra el componente nuevo solo cuando corresponde registrar entrada.
- `ui/view/administrador/GestionFaenasView.kt`: formulario con coordenadas.
- `modelo/Faena.kt`, `modelo/Asistencia.kt`, `datos/local/AppDataBase.kt`: campos y migraciones de Room.
- `datos/local/dao/AsistenciaDAO.kt`, repositorio y ViewModel: reglas transaccionales de entrada/salida.

## Base de datos y respaldo

La base se actualiza de versión 5 a versión 8. Se incluyen rutas 5→8, 6→8 y 7→8; no hay `fallbackToDestructiveMigration`. Se probaron con SQLite las transformaciones estructurales desde las versiones **5 y 6**, verificando conservación de usuarios, faenas y marcaciones. La compatibilidad completa con instalaciones reales, especialmente al migrar desde **Bluetooth versión 7**, requiere una prueba en Android: las migraciones 6/7→8 conservan los datos básicos, pero **descartan columnas adicionales exclusivas de versiones de desarrollo** y no trasladan el módulo de solicitudes excepcionales. **Haz copia de seguridad antes de actualizar un dispositivo con datos importantes.**

## Estado de verificación

- ✅ Código Kotlin de las reglas de bloqueo y geocerca compilado y ejecutado en aislamiento.
- ✅ SQL de migraciones 5→8 y 6→8 comprobado mediante SQLite de escritorio.
- ✅ Se incluye test de reglas y test DAO para bloquear entradas sin validar (requieren Gradle/Android para ejecutarse).
- ⚠️ **No se ha podido ejecutar Android Studio ni compilar la aplicación completa** en este entorno porque no hay Android SDK. Es necesario validar con **Build > Make Project** y probar permisos, GPS y Room en un teléfono.
- ⚠️ Los cambios **no se han subido a GitHub**: la conexión de escritura no tiene acceso (403).
