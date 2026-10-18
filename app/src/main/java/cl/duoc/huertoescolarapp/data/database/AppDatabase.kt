package cl.duoc.huertoescolarapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.huertoescolarapp.data.dao.CultivoDao
import cl.duoc.huertoescolarapp.data.dao.SectorDao
import cl.duoc.huertoescolarapp.data.dao.TareaDao
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import cl.duoc.huertoescolarapp.data.entity.TareaEntity

@Database(entities = [SectorEntity::class, CultivoEntity::class, TareaEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sectorDao(): SectorDao
    abstract fun cultivoDao(): CultivoDao
    abstract fun tareaDao(): TareaDao

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