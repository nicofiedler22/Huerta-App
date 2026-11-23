package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Cada vez que el administrador aprueba o rechaza una tarea se guarda una fila: quién, cuándo y el resultado
@Entity(
    tableName = "validaciones",
    foreignKeys = [
        ForeignKey(
            entity = TareaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tareaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["validadorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tareaId"]), Index(value = ["validadorId"])]
)
data class ValidacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tareaId: Long,
    val validadorId: Long,
    val resultado: String, // Validada o Rechazada
    val motivo: String = "",
    val fecha: String
)
