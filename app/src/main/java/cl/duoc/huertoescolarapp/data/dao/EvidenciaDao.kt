package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.huertoescolarapp.data.entity.EvidenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEvidencia(evidencia: EvidenciaEntity): Long

    @Query("SELECT * FROM evidencias ORDER BY id")
    fun obtenerEvidencias(): Flow<List<EvidenciaEntity>>

    @Query("SELECT * FROM evidencias WHERE tareaId = :idTarea ORDER BY id")
    fun obtenerEvidenciasPorTarea(idTarea: Long): Flow<List<EvidenciaEntity>>
}
