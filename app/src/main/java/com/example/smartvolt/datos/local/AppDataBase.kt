package com.example.smartvolt.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartvolt.datos.local.dao.AsistenciaDAO
import com.example.smartvolt.datos.local.dao.FaenaDAO
import com.example.smartvolt.datos.local.dao.UsuarioDAO
import com.example.smartvolt.modelo.Asistencia
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.modelo.Usuario

@Database(
    entities = [
        Usuario::class,
        Asistencia::class,
        Faena::class
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDAO(): UsuarioDAO

    abstract fun asistenciaDAO(): AsistenciaDAO

    abstract fun faenaDAO(): FaenaDAO

    companion object {

        @Volatile
        private var INSTANCIA: AppDataBase? = null

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (!tieneColumna(db, "usuarios", "supervisorId")) {
                    db.execSQL("ALTER TABLE usuarios ADD COLUMN supervisorId TEXT DEFAULT NULL")
                }
            }
        }

        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS faenas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        ubicacion TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        fechaInicio TEXT NOT NULL,
                        fechaTermino TEXT NOT NULL,
                        activa INTEGER NOT NULL
                    )
                """.trimIndent())

                if (!tieneColumna(db, "usuarios", "faenaId")) {
                    db.execSQL("ALTER TABLE usuarios ADD COLUMN faenaId INTEGER DEFAULT NULL")
                }
            }
        }

        private fun tieneColumna(db: SupportSQLiteDatabase, tabla: String, columna: String): Boolean {
            db.query("PRAGMA table_info($tabla)").use { cursor ->
                while (cursor.moveToNext()) {
                    if (cursor.getString(1) == columna) return true
                }
            }
            return false
        }

        // Master utilizaba la versión 5. Agregamos coordenadas y la marca DEMO sin borrar datos.
        private val MIGRACION_5_8 = object : Migration(5, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE faenas ADD COLUMN latitud REAL")
                db.execSQL("ALTER TABLE faenas ADD COLUMN longitud REAL")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN metodoRegistro TEXT NOT NULL DEFAULT 'LEGADO'")
            }
        }

        // Compatibilidad con instalaciones anteriores de desarrollo (esquemas 6 y 7).
        private val MIGRACION_6_8 = object : Migration(6, 8) {
            override fun migrate(db: SupportSQLiteDatabase) = reconstruirTablas(db, false)
        }
        private val MIGRACION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) = reconstruirTablas(db, true)
        }

        private fun reconstruirTablas(db: SupportSQLiteDatabase, tieneMetodo: Boolean) {
            // Esquemas antiguos tienen columnas adicionales incompatibles con master.
            db.execSQL("""CREATE TABLE faenas_nuevas (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT NOT NULL, ubicacion TEXT NOT NULL, descripcion TEXT NOT NULL,
                fechaInicio TEXT NOT NULL, fechaTermino TEXT NOT NULL, activa INTEGER NOT NULL,
                latitud REAL, longitud REAL
            )""".trimIndent())
            db.execSQL("""INSERT INTO faenas_nuevas
                (id,nombre,ubicacion,descripcion,fechaInicio,fechaTermino,activa,latitud,longitud)
                SELECT id,nombre,ubicacion,descripcion,fechaInicio,fechaTermino,activa,latitud,longitud
                FROM faenas""".trimIndent())
            db.execSQL("DROP TABLE faenas")
            db.execSQL("ALTER TABLE faenas_nuevas RENAME TO faenas")

            db.execSQL("""CREATE TABLE asistencias_nuevas (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                usuarioId TEXT NOT NULL, fechaHora INTEGER NOT NULL, tipo TEXT NOT NULL,
                metodoRegistro TEXT NOT NULL DEFAULT 'LEGADO',
                FOREIGN KEY(usuarioId) REFERENCES usuarios(id)
                ON UPDATE NO ACTION ON DELETE CASCADE
            )""".trimIndent())
            val origenMetodo = if (tieneMetodo) "metodoRegistro" else "'LEGADO'"
            db.execSQL("""INSERT INTO asistencias_nuevas
                (id,usuarioId,fechaHora,tipo,metodoRegistro)
                SELECT id,usuarioId,fechaHora,tipo,$origenMetodo FROM asistencias""".trimIndent())
            db.execSQL("DROP TABLE asistencias")
            db.execSQL("ALTER TABLE asistencias_nuevas RENAME TO asistencias")
            db.execSQL("CREATE INDEX index_asistencias_usuarioId ON asistencias (usuarioId)")
            // Otros metadatos demo de versiones anteriores no forman parte de master.
        }

        fun obtenerBaseDatos(context: Context): AppDataBase {
            return INSTANCIA ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "asistencia_faena_db"
                )
                    .addMigrations(
                        MIGRACION_3_4,
                        MIGRACION_4_5,
                        MIGRACION_5_8,
                        MIGRACION_6_8,
                        MIGRACION_7_8
                    )
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}
