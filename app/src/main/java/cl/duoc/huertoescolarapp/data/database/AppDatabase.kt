package cl.duoc.huertoescolarapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.huertoescolarapp.data.dao.CultivoDao
import cl.duoc.huertoescolarapp.data.dao.EvidenciaDao
import cl.duoc.huertoescolarapp.data.dao.ReporteProblemaDao
import cl.duoc.huertoescolarapp.data.dao.SectorDao
import cl.duoc.huertoescolarapp.data.dao.TareaDao
import cl.duoc.huertoescolarapp.data.dao.UsuarioDao
import cl.duoc.huertoescolarapp.data.dao.ValidacionDao
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import cl.duoc.huertoescolarapp.data.entity.EvidenciaEntity
import cl.duoc.huertoescolarapp.data.entity.ReporteProblemaEntity
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import cl.duoc.huertoescolarapp.data.entity.UsuarioEntity
import cl.duoc.huertoescolarapp.data.entity.ValidacionEntity

@Database(
    entities = [
        UsuarioEntity::class,
        SectorEntity::class,
        CultivoEntity::class,
        TareaEntity::class,
        ValidacionEntity::class,
        EvidenciaEntity::class,
        ReporteProblemaEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun sectorDao(): SectorDao
    abstract fun cultivoDao(): CultivoDao
    abstract fun tareaDao(): TareaDao
    abstract fun validacionDao(): ValidacionDao
    abstract fun evidenciaDao(): EvidenciaDao
    abstract fun reporteProblemaDao(): ReporteProblemaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huerto_escolar_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
