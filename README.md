[README.md](https://github.com/user-attachments/files/34797828/README.md)
# ⚡ Smart-Volt

**Control de asistencia en faena con validación de ubicación GPS y Bluetooth.**

Smart-Volt es una aplicación Android nativa que permite a los operarios registrar su entrada y salida de una faena, a los supervisores monitorear a su equipo y a los administradores gestionar usuarios y faenas. La marcación de **entrada** solo se habilita cuando el trabajador demuestra que está físicamente en el lugar: Bluetooth encendido, baliza detectada y GPS real dentro del radio de la faena.

> ⚠️ **Proyecto académico / prototipo.** La baliza Bluetooth es **simulada** (ver [Alcance y limitaciones](#-alcance-y-limitaciones)). No debe usarse como sistema certificado de asistencia.

---

## 📑 Contenido

- [Funcionalidades](#-funcionalidades)
- [Roles del sistema](#-roles-del-sistema)
- [Cómo funciona la validación de entrada](#-cómo-funciona-la-validación-de-entrada)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Base de datos](#-base-de-datos)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Usuarios de prueba](#-usuarios-de-prueba)
- [Pruebas](#-pruebas)
- [Alcance y limitaciones](#-alcance-y-limitaciones)
- [Equipo](#-equipo)

---

## ✨ Funcionalidades

- 🔐 **Inicio de sesión por ID y contraseña**, con redirección automática según el rol.
- 📍 **Validación de entrada con GPS real**: rechaza ubicaciones simuladas, lecturas antiguas, baja precisión y distancias fuera de rango.
- 📶 **Verificación de Bluetooth** del teléfono y baliza de demostración.
- 🕒 **Alternancia automática entrada/salida**: el sistema decide la siguiente marcación según la última registrada.
- 📋 **Historial de asistencia** por trabajador, con etiqueta del método de registro.
- 👥 **Gestión de usuarios** (crear, editar, activar/desactivar, eliminar, asignar supervisor y faena).
- 🏗️ **Gestión de faenas** con coordenadas, estado y fechas.
- 📊 **Panel del supervisor**: personal a cargo, asistencia del equipo y reportes de actividad.
- 🌱 **Datos iniciales idempotentes**: dos faenas de prueba y tres usuarios se crean al abrir la app sin duplicarse.
- ✨ **Splash animado** e identidad visual propia (paleta verde Smart-Volt).

## 👤 Roles del sistema

| Rol | Qué puede hacer |
| --- | --- |
| **Operario** | Ver su faena asignada, validar y registrar entrada/salida, consultar historial y perfil. |
| **Supervisor** | Ver su personal a cargo, la asistencia de su equipo y reportes de estado (en jornada, entradas, salidas, sin marcaciones). |
| **Administrador** | Crear y administrar usuarios y faenas, asignar supervisores y faenas a los operarios. |

## 🛰️ Cómo funciona la validación de entrada

Para habilitar **Registrar entrada** deben cumplirse **todas** estas condiciones:

1. ✅ El operario tiene una **faena activa** asignada y con coordenadas configuradas.
2. ✅ **Bluetooth** del teléfono encendido (con permiso `BLUETOOTH_CONNECT` en Android 12+).
3. ✅ **Baliza detectada** (en esta versión, mediante un interruptor de demostración).
4. ✅ **GPS válido**, con estas reglas:

| Regla | Valor |
| --- | --- |
| Permiso de ubicación | Precisa (`ACCESS_FINE_LOCATION`) |
| Ubicación simulada (mock) | Rechazada |
| Antigüedad de la lectura | ≤ 15 segundos |
| Precisión del GPS | ≤ 30 m |
| Distancia a la faena + margen de error | ≤ 100 m |

Además:

- El GPS se **vuelve a verificar al presionar el botón**, nunca se reutiliza una lectura anterior.
- La **capa de datos (DAO) también exige la validación**: aunque alguien saltara la interfaz, Room rechaza una entrada no validada.
- La marcación se ejecuta dentro de **una transacción** y un `Mutex` evita dobles pulsaciones.
- Si el estado de la jornada cambió mientras se marcaba, la operación se rechaza (`tipoEsperado`).

## 🧰 Tecnologías

| Área | Tecnología |
| --- | --- |
| Lenguaje | Kotlin 2.2.10 |
| Interfaz | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Arquitectura | MVVM + Repositorios |
| Base de datos | Room 2.7.0 (SQLite) con KSP |
| Concurrencia | Corrutinas y Flow / StateFlow |
| Seguridad | PBKDF2 (SHA-256) con sal aleatoria, 120.000 iteraciones |
| Sensores | `LocationManager` (GPS) y `BluetoothManager` |
| Pruebas | JUnit 4, Espresso / AndroidX Test |
| Build | Gradle (Kotlin DSL), AGP 9.4.1 · `minSdk 24` · `targetSdk 37` |

## 🏛️ Arquitectura

```
app/src/main/java/com/example/smartvolt/
├── MainActivity.kt          # Punto de entrada, splash y datos iniciales
├── modelo/                  # Entidades Room: Usuario, Faena, Asistencia
├── datos/
│   ├── local/               # AppDataBase, migraciones, DAO, FaenasIniciales
│   ├── repositorio/         # Repositorios (acceso a datos)
│   └── seguridad/           # GestorContrasenas (PBKDF2)
├── servicios/               # ValidacionEntrada (GPS/Bluetooth) y ReglasEntrada
├── viewmodel/               # Login, Usuario, Faena y Asistencia (+ factories)
├── ui/
│   ├── navigation/          # Rutas y NavHost
│   ├── components/          # Componentes reutilizables
│   ├── theme/               # Colores, tipografía y tema
│   └── view/                # Pantallas: splash, login, operario, supervisor, administrador
└── util/                    # Utilidades (formato de fecha)
```

El flujo es **Vista (Compose) → ViewModel → Repositorio → DAO → Room**, con el estado de la interfaz expuesto mediante `StateFlow`.

## 🗄️ Base de datos

Room con **3 tablas** (versión actual: **8**), esquema exportado en `app/schemas/`.

| Tabla | Descripción | Campos principales |
| --- | --- | --- |
| `usuarios` | Trabajadores del sistema | `id` (PK), `contrasenaHash`, `nombreCompleto`, `cargo`, `rol`, `activo`, `supervisorId`, `faenaId` |
| `faenas` | Lugares de trabajo | `id` (PK), `nombre`, `ubicacion`, `descripcion`, `fechaInicio`, `fechaTermino`, `activa`, `latitud`, `longitud` |
| `asistencias` | Marcaciones | `id` (PK), `usuarioId` (FK → usuarios, `CASCADE`), `fechaHora`, `tipo`, `metodoRegistro` |

- Las contraseñas **nunca se guardan en texto plano**: se almacena un hash con formato `v2$algoritmo$iteraciones$sal$hash`.
- Migraciones `3→4`, `4→5`, `5→8`, `6→8` y `7→8`, **sin** `fallbackToDestructiveMigration`: los datos de instalaciones anteriores se conservan.
- Las marcaciones de entrada se guardan con `metodoRegistro = DEMO_BLE_GPS` para identificarlas como demostración.

### Faenas iniciales de prueba

| Faena | Latitud | Longitud |
| --- | ---: | ---: |
| DUOC PADRE ALONSO DE OVALLE | -33.446327 | -70.657515 |
| PLAZA CONSTITUCION | -33.441754 | -70.654102 |

## 🚀 Instalación y ejecución

### Requisitos

- Android Studio (versión reciente compatible con AGP 9.x) y JDK 11 o superior
- **Teléfono Android físico** con Bluetooth y GPS (recomendado probar al aire libre)

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/<tu-usuario>/Smart-Volt.git
   ```
2. Abre la carpeta en Android Studio con **File > Open** y espera la sincronización de Gradle.
3. Ejecuta **Build > Make Project**.
4. Conecta el teléfono y presiona **Run**.

### Probar el registro de entrada

1. Inicia sesión como **administrador** → **Gestión de usuarios** → asigna una de las faenas iniciales al operario.
2. Cierra sesión e ingresa como **operario** → **Control de asistencia**.
3. Pulsa **Activar / comprobar Bluetooth** y acepta los permisos.
4. Activa **Simular baliza detectada**.
5. Pulsa **Activar / comprobar ubicación** y concede ubicación **precisa**.
6. Si estás a menos de 100 m de la faena, pulsa **Registrar entrada**.
7. Revisa el resultado en **Historial**. La siguiente marcación será **Registrar salida**.

> 💡 En un **emulador** la ubicación inyectada es marcada como simulada por Android y **la app la rechaza a propósito**. Para probar el flujo exitoso usa un teléfono físico cerca de las coordenadas de la faena, o edita la faena con las coordenadas de donde estés.

## 🔑 Usuarios de prueba

Creados automáticamente al abrir la app por primera vez:

| Rol | ID | Contraseña |
| --- | --- | --- |
| Operario | `OP-001` | `123456` |
| Supervisor | `SU-001` | `123456` |
| Administrador | `AD-001` | `123456` |

`OP-001` queda asignado a `SU-001` como supervisor. **Estas credenciales son solo para pruebas.**

## 🧪 Pruebas

```bash
# Pruebas unitarias (reglas de entrada y radio GPS)
./gradlew testDebugUnitTest

# Pruebas instrumentadas (DAO de asistencia, requiere dispositivo/emulador)
./gradlew connectedDebugAndroidTest
```

- `ReglasEntradaTest`: verifica que se exijan todos los requisitos y el cálculo de distancia + margen de precisión.
- `AsistenciaDAOTest`: verifica que la capa de datos bloquee entradas sin validar.

## ⚠️ Alcance y limitaciones

Esto es un prototipo y conviene ser transparentes:

- **La baliza Bluetooth es simulada.** El Bluetooth del teléfono se verifica de verdad, pero no se escanea ninguna baliza BLE física. Por eso la entrada no acredita presencia vía Bluetooth.
- `Location.isMock` **no detecta todos los tipos de suplantación de GPS**.
- La **salida** no exige Bluetooth ni GPS en esta versión.
- No hay sincronización con servidor: los datos viven en el dispositivo.
- Haz una **copia de seguridad** antes de actualizar un dispositivo con datos importantes: las migraciones desde versiones de desarrollo (6 y 7) descartan columnas exclusivas de esas versiones.

### 🔭 Próximos pasos

- Detección real de balizas BLE (escaneo con `BluetoothLeScanner`).
- Backend con sincronización y respaldo en la nube.
- Validación GPS también en la salida.
- Reportes exportables (PDF / Excel) para supervisores.
- Unificar la política de contraseñas entre login y gestión de usuarios.

## 👥 Equipo

| Integrante | Área |
| --- | --- |
| _Nombre 1_ | Lógica y validaciones |
| _Nombre 2_ | Diseño de interfaz |
| _Nombre 3_ | Base de datos, Bluetooth y GPS |

> Proyecto desarrollado como trabajo académico — _Institución / asignatura / año_.
