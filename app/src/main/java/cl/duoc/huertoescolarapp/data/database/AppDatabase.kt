package cl.duoc.huertoescolarapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.huertoescolarapp.data.dao.SectorDao
import cl.duoc.huertoescolarapp.data.entity.SectorEntity

@Database(entities = [SectorEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sectorDao(): SectorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huerto_escolar_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}