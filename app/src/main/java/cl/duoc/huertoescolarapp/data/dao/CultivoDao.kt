package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCultivo(cultivo: CultivoEntity): Long

    @Update
    suspend fun actualizarCultivo(cultivo: CultivoEntity)

    @Delete
    suspend fun eliminarCultivo(cultivo: CultivoEntity)

    @Query("SELECT * FROM cultivos")
    fun obtenerTodosLosCultivos(): Flow<List<CultivoEntity>>

    @Query("SELECT * FROM cultivos WHERE sectorId = :idSector")
    fun obtenerCultivosPorSector(idSector: Long): Flow<List<CultivoEntity>>
}
