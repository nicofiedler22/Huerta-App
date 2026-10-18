package cl.duoc.huertoescolarapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TareaEntity)

    @Update
    suspend fun actualizarTarea(tarea: TareaEntity)

    @Query("SELECT * FROM tareas")
    fun obtenerTodasLasTareas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE responsable = :nombreEstudiante")
    fun obtenerTareasPorEstudiante(nombreEstudiante: String): Flow<List<TareaEntity>>
}