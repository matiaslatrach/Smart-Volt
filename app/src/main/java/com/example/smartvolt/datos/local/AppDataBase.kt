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
import com.example.smartvolt.modelo.SolicitudExcepcion
import com.example.smartvolt.datos.local.dao.SolicitudExcepcionDAO

@Database(
    entities = [
        Usuario::class,
        Asistencia::class,
        Faena::class,
        SolicitudExcepcion::class
    ],
    version = 7,
    exportSchema = true
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDAO(): UsuarioDAO

    abstract fun asistenciaDAO(): AsistenciaDAO

    abstract fun faenaDAO(): FaenaDAO
    abstract fun solicitudExcepcionDAO(): SolicitudExcepcionDAO

    companion object {

        @Volatile
        private var INSTANCIA: AppDataBase? = null

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE usuarios ADD COLUMN supervisorId TEXT DEFAULT NULL"
                )
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

                db.execSQL(
                    "ALTER TABLE usuarios ADD COLUMN faenaId INTEGER DEFAULT NULL"
                )
            }
        }


        /** Migración desde la versión actual 5, sin eliminar las asistencias existentes. */
        private val MIGRACION_5_7 = object : Migration(5, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE faenas ADD COLUMN latitud REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE faenas ADD COLUMN longitud REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE faenas ADD COLUMN beaconId TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN faenaId INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN metodoRegistro TEXT NOT NULL DEFAULT 'LEGADO'")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN validacionBluetooth TEXT NOT NULL DEFAULT 'NO_VERIFICADO'")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN validacionGps TEXT NOT NULL DEFAULT 'NO_VERIFICADO'")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN distanciaMetros REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN precisionMetros REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE asistencias ADD COLUMN solicitudId INTEGER DEFAULT NULL")
                crearSolicitudes(db)
            }
        }

        /** El esquema 6 histórico tenía columnas distintas: se preservan los datos comunes. */
        private val MIGRACION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE faenas_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL, ubicacion TEXT NOT NULL, descripcion TEXT NOT NULL, fechaInicio TEXT NOT NULL, fechaTermino TEXT NOT NULL, activa INTEGER NOT NULL, latitud REAL, longitud REAL, beaconId TEXT)""")
                db.execSQL("""INSERT INTO faenas_new (id,nombre,ubicacion,descripcion,fechaInicio,fechaTermino,activa,latitud,longitud) SELECT id,nombre,ubicacion,descripcion,fechaInicio,fechaTermino,activa,latitud,longitud FROM faenas""")
                db.execSQL("DROP TABLE faenas")
                db.execSQL("ALTER TABLE faenas_new RENAME TO faenas")
                db.execSQL("""CREATE TABLE asistencias_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, usuarioId TEXT NOT NULL, fechaHora INTEGER NOT NULL, tipo TEXT NOT NULL, faenaId INTEGER, metodoRegistro TEXT NOT NULL DEFAULT 'LEGADO', validacionBluetooth TEXT NOT NULL DEFAULT 'NO_VERIFICADO', validacionGps TEXT NOT NULL DEFAULT 'NO_VERIFICADO', distanciaMetros REAL, precisionMetros REAL, solicitudId INTEGER, FOREIGN KEY(usuarioId) REFERENCES usuarios(id) ON UPDATE NO ACTION ON DELETE CASCADE)""")
                db.execSQL("""INSERT INTO asistencias_new (id,usuarioId,fechaHora,tipo,distanciaMetros) SELECT id,usuarioId,fechaHora,tipo,distanciaMetros FROM asistencias""")
                db.execSQL("DROP TABLE asistencias")
                db.execSQL("ALTER TABLE asistencias_new RENAME TO asistencias")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_asistencias_usuarioId ON asistencias(usuarioId)")
                crearSolicitudes(db)
            }
        }

        private fun crearSolicitudes(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TABLE IF NOT EXISTS solicitudes_excepcion (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                usuarioId TEXT NOT NULL,
                supervisorId TEXT NOT NULL,
                faenaId INTEGER NOT NULL,
                tipo TEXT NOT NULL,
                fechaSolicitud INTEGER NOT NULL,
                motivo TEXT NOT NULL,
                bluetoothValido INTEGER NOT NULL,
                gpsValido INTEGER NOT NULL,
                estado TEXT NOT NULL,
                fechaResolucion INTEGER,
                observacion TEXT
            )""")
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
                        MIGRACION_5_7,
                        MIGRACION_6_7
                    )
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}
