package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSector(sector: SectorEntity)

    @Query("SELECT * FROM sectores")
    fun obtenerSectores(): Flow<List<SectorEntity>>
}