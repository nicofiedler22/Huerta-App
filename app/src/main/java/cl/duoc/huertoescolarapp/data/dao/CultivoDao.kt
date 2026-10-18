package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCultivo(cultivo: CultivoEntity)

    @Query("SELECT * FROM cultivos WHERE sectorId = :idSector")
    fun obtenerCultivosPorSector(idSector: Long): Flow<List<CultivoEntity>>
}