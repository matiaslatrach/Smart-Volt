# Smart-Volt — Login simplificado

## Cambio realizado

Se simplificó únicamente `app/src/main/java/com/example/smartvolt/ui/view/login/LoginView.kt`.

La pantalla ahora muestra:
- Marca Smart-Volt coherente con el splash existente.
- Campo «ID del trabajador».
- Campo «Contraseña» con opción de mostrar/ocultar.
- Botón «Iniciar sesión» y mensajes de error cuando corresponda.

Se quitaron los encabezados largos, slogans, tarjeta de acceso seguro, información de versión y elementos decorativos que recargaban el inicio de sesión.

## Funcionalidad conservada

El `LoginViewModel`, la validación de contraseñas, la navegación por roles y el splash **no se modificaron**. La contraseña sigue siendo numérica de 6 dígitos, como exige el ViewModel actual. El botón solo se habilita al ingresar ambos campos y se desactiva mientras se verifica el acceso.

Se reutiliza `smartVoltTextFieldColors()` para que el texto escrito sea oscuro y legible, incluso si el teléfono está configurado en modo oscuro.

## Cómo usar

1. Abre la carpeta del proyecto (la que contiene `settings.gradle.kts`) en Android Studio.
2. Ejecuta `Build > Make Project`.
3. Ejecuta la app y verifica el splash y el nuevo login.

## Alcance de la verificación

Se revisó la estructura del proyecto y la integridad del ZIP, y se compararon los archivos del proyecto con la versión de splash. **No se realizó una compilación Android real**, porque este entorno no dispone del SDK de Android. El repositorio GitHub no se modificó.
