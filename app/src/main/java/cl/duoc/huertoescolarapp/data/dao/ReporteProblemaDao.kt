package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolarapp.data.entity.ReporteProblemaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteProblemaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReporte(reporte: ReporteProblemaEntity): Long

    @Update
    suspend fun actualizarReporte(reporte: ReporteProblemaEntity)

    @Query("SELECT * FROM reportes_problema ORDER BY id DESC")
    fun obtenerReportes(): Flow<List<ReporteProblemaEntity>>
}
