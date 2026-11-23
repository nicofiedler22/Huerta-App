package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = SectorEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectorId"],
            onDelete = ForeignKey.CASCADE
        ),
        // El responsable ahora es un usuario real, ya no un texto
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["responsableId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sectorId"]), Index(value = ["responsableId"])]
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sectorId: Long,
    val responsableId: Long, // Estudiante asignado
    val titulo: String,
    val descripcion: String,
    val tipo: String, // Riego, Siembra, Mantenimiento, Observación
    val fechaProgramada: String,
    val estado: String = "Pendiente", // Pendiente, En desarrollo, Pendiente de validación, Validada, Rechazada
    val comentariosEstudiante: String = ""
)
