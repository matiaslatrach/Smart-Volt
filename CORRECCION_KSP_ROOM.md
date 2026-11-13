# Corrección del error KSP en Room

El archivo `FaenaDAO.kt` tenía una consulta `INSERT INTO faenas ... SELECT ... WHERE NOT EXISTS` con tipo de retorno `Int`.

Para las sentencias `INSERT` declaradas con `@Query`, Room admite `Long` o `Unit`, no `Int`. Se corrigió el retorno a `Long`. El resultado numérico no se utiliza en `FaenasIniciales.kt`, por lo que no cambia el comportamiento de carga de las dos faenas.

Este ZIP conserva el resto del proyecto exactamente como el ZIP anterior.

## Probar en Android Studio
1. Extrae el ZIP en otra carpeta para no mezclar archivos previos.
2. Abre la carpeta Smart-Volt-Master-Demo-Bluetooth-GPS.
3. En Android Studio: Build > Clean Project, luego Build > Rebuild Project.
4. Si continúa el error, abre Build Output y busca la **primera** línea de error `[ksp]` o `e:`, que suele estar por encima del resumen `PROCESSING_ERROR`. Envíame esa parte del log.

## Validación
Se comprobó el cambio del tipo de retorno, que el ZIP no está corrupto y el SQL de creación sin duplicados en SQLite. No se ejecutó una compilación Android completa en este entorno.
