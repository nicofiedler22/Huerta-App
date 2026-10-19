package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.huertoescolarapp.data.entity.ValidacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ValidacionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarValidacion(validacion: ValidacionEntity): Long

    @Query("SELECT * FROM validaciones ORDER BY id")
    fun obtenerValidaciones(): Flow<List<ValidacionEntity>>

    @Query("SELECT * FROM validaciones WHERE tareaId = :idTarea ORDER BY id")
    fun obtenerValidacionesPorTarea(idTarea: Long): Flow<List<ValidacionEntity>>
}
