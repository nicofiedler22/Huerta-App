package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sectorId: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: String, // Riego, Siembra, Mantenimiento, Observación
    val fechaProgramada: String,
    val responsable: String, // Estudiante asignado
    val estado: String = "Pendiente", // Pendiente, En desarrollo, Pendiente de validación, Validada, Rechazada
    val comentariosEstudiante: String = "",
    val motivoRechazo: String = ""
)