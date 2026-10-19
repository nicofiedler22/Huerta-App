package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSector(sector: SectorEntity): Long

    @Update
    suspend fun actualizarSector(sector: SectorEntity)

    @Delete
    suspend fun eliminarSector(sector: SectorEntity)

    @Query("SELECT * FROM sectores")
    fun obtenerSectores(): Flow<List<SectorEntity>>

    @Query("SELECT * FROM sectores WHERE id = :idSector")
    fun obtenerSectorPorId(idSector: Long): Flow<SectorEntity?>
}
